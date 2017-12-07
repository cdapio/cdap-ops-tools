package co.cask.tools;

/*
 * Copyright © 2017 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.RegionLoad;
import org.apache.hadoop.hbase.ServerLoad;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

public class MergeCommands {
  private static final int LIMIT_MB = 100*1024;

  private Admin getAdmin() throws IOException {
    Connection connection = ConnectionFactory.createConnection();
    return connection.getAdmin();
  }

  private Queue<MergeRegions> getAdjacentRegions(String tableName, int sizeLimt) throws IOException {
    Admin admin = getAdmin();
    TableName table = TableName.valueOf(tableName);
    List<HRegionInfo> hRegionInfos = admin.getTableRegions(table);
    // sort the hregionInfos by startKey byte array
    hRegionInfos.sort(new HRegionInfoComparator());

    // iterate the entries, if the size is greater than limit, remove from the list
    Iterator<HRegionInfo> infoIterator = hRegionInfos.iterator();
    SortedMap<byte[], Integer> regionLoadMap = getRegionSizes();
    System.out.println("Size of region load map " + regionLoadMap.size());
    Queue<MergeRegions> mergeRegionsQueue = new LinkedList<>();
    MergeRegions mergeRegions;
    while ((mergeRegions = getNextAdjacentRegions(infoIterator, regionLoadMap, sizeLimt, admin)) != null) {
      mergeRegionsQueue.add(mergeRegions);
    }
    return mergeRegionsQueue;
  }


  private MergeRegions getNextAdjacentRegions(Iterator<HRegionInfo> infoIterator,
                                              SortedMap<byte[], Integer> regionLoadMap, int sizeLimt, Admin admin) {
    // if two adjacent entries within limit is present, take them and create MergeRegions object
    while (infoIterator.hasNext()) {
      HRegionInfo hRegionInfo1 = infoIterator.next();
      if (withinSizeLimit(hRegionInfo1, regionLoadMap, sizeLimt)) {
        if (infoIterator.hasNext()) {
          HRegionInfo hRegionInfo2 = infoIterator.next();
          if (withinSizeLimit(hRegionInfo2, regionLoadMap, sizeLimt)) {
            return new MergeRegions(hRegionInfo1, hRegionInfo2, regionLoadMap.get(hRegionInfo1.getRegionName()),
                                    regionLoadMap.get(hRegionInfo2.getRegionName()), admin);
          }
        }
      }
    }
    return null;
  }

  private boolean withinSizeLimit(HRegionInfo hRegionInfo, SortedMap<byte[], Integer> regionLoadMap, int sizeLimit) {
    if (regionLoadMap.containsKey(hRegionInfo.getRegionName()) &&
      regionLoadMap.get(hRegionInfo.getRegionName()) < sizeLimit) {
      return true;
    }
    System.out.println(String.format("Skipping region %s, size : %s MB larger than limit",
                                     Bytes.toStringBinary(hRegionInfo.getRegionName()),
                                     regionLoadMap.get(hRegionInfo.getRegionName()), LIMIT_MB));
    return false;
  }

  class HRegionInfoComparator implements Comparator<HRegionInfo> {
    @Override
    public int compare(HRegionInfo o1, HRegionInfo o2) {
      return Bytes.compareTo(o1.getStartKey(), o2.getStartKey());
    }
  }

  private SortedMap<byte[], Integer> getRegionSizes() throws IOException {
    final Admin admin = getAdmin();
    final ClusterStatus clusterStatus = admin.getClusterStatus();
    SortedMap<byte[], Integer> regionLoadMap = new TreeMap<>(Bytes.BYTES_COMPARATOR);
    for (ServerName serverName : clusterStatus.getServers()) {
      final ServerLoad serverLoad = clusterStatus.getLoad(serverName);

      for (Map.Entry<byte[], RegionLoad> entry : serverLoad.getRegionsLoad().entrySet()) {
        regionLoadMap.put(entry.getKey(), entry.getValue().getStorefileSizeMB());
      }
    }
    return regionLoadMap;
  }

  private void printMerges(String tableName,
                           int numMerges, int sizeLimit) throws IOException, InterruptedException {
    Queue<MergeRegions> mergeRegionsQueue = getAdjacentRegions(tableName, sizeLimit);
    System.out.println("Printing Commands");
    System.out.println("------------------");
    while (!mergeRegionsQueue.isEmpty() && numMerges-- > 0) {
      System.out.println(mergeRegionsQueue.poll().toString());
    }
  }

  public static void main(String[] args) {
    if (args.length == 0 || args[0].equals("help")) {
      System.out.println("Usage : com.appender.app.MergeCommands <table-name> <number-of-merges-to-print> <size-limit>");
      return;
    }

    if (args.length < 2) {
      System.out.println("Expected table name as 1st parameter and number of merges to print as 2nd, " +
                           "Optionally you can specify the size limit of regions to merge as 3rd Parameter, " +
                           "by default it is 100GB");
      return;
    }

    try {
      int limit = LIMIT_MB;
      if (args.length == 3) {
        limit = Integer.parseInt(args[2]);
      }
      MergeCommands mergeCommands = new MergeCommands();
      mergeCommands.printMerges(args[0], Integer.parseInt(args[1]), limit);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

