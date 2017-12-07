package co.cask.tools;

import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.client.Admin;

public class MergeRegions {
  HRegionInfo regionNameA;
  HRegionInfo regionNameB;
  Admin admin;
  int count = 5;
  int size1;
  int size2;

  public MergeRegions(HRegionInfo regionNameA, HRegionInfo regionNameB, int size1, int size2, Admin admin) {
    this.regionNameA = regionNameA;
    this.regionNameB = regionNameB;
    this.admin = admin;
    this.size1 = size1;
    this.size2 = size2;
  }

  /**
   * merges the two regions
   */
  public void merge() {
    // no - op
    System.out.println(String.format("Merge called for regions %s and %s",
                                     regionNameA.getEncodedName(), regionNameB.getEncodedName()));
  }

  /**
   * checks if a region with startKey of regionNameA and endKey of regionNameB is available
   * if its available, it prints the details of new region and returns true, else return false
   * @return
   */
  public boolean isDone() {
    if (count == 0) {
      System.out.println(String.format("Merged regions %s and %s",
                                       regionNameA.getEncodedName(), regionNameB.getEncodedName()));
      return true;
    }
    count--;
    return false;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("Command : merge_region '%s', '%s'",
                                 regionNameA.getEncodedName(), regionNameB.getEncodedName()));
    builder.append(String.format("\n Region A : Name : %s Startkey : %s EndKey : %s Size : %s MB",
                                 regionNameA.getEncodedName(), Bytes.toStringBinary(regionNameA.getStartKey()),
                                 Bytes.toStringBinary(regionNameA.getEndKey()), size1));
    builder.append(String.format("\n Region B : Name : %s Startkey : %s EndKey : %s Size : %s MB",
                                 regionNameB.getEncodedName(), Bytes.toStringBinary(regionNameB.getStartKey()),
                                 Bytes.toStringBinary(regionNameB.getEndKey()), size2));
    return builder.toString();
  }
}
