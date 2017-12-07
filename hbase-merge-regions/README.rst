=========================
HBase Merging helper Tool
=========================

Tool to find regions to merge in hbase for tables with many regions.



Usage
=======
1) Build the project

.. code:: xml

  mvn clean package -DskipTests

2) Once you have the jar in the cluster, you can execute the following to run 

.. code:: xml

  java -cp hbase-merge-1.0-SNAPSHOT.jar:`hbase classpath`  co.cask.tools.MergeCommands [table-name] [number of merges to print] <optional-limit-on-region size>

Parameters:

table-name : name of the hbase table, including the namespace

number of merges to print : limit on the number of merge commands to print 

optional-limit-on-region size : By default 100GB, otherwise can be set higher or lower by using this parameter. regions larger than this size will be skipped.

Example Run :

.. code:: console

  sudo -u hbase java -cp hbase-merge-1.0-SNAPSHOT.jar:`hbase classpath`  co.cask.tools.MergeCommands cdap_system:metrics.v2.table.ts.3600 2

Example output :

.. code:: console

	Command : merge_region '8ee2a0b1ca1510723f240ba993403690', '299a29d0e495b6b6a89a7482e5b28277'
	 Region A : Name : 8ee2a0b1ca1510723f240ba993403690 Startkey :  EndKey : \x00\x00\x00\x02 Size : 20 MB
	 Region B : Name : 299a29d0e495b6b6a89a7482e5b28277 Startkey : \x00\x00\x00\x02 EndKey : \x00\x00\x00\x04 Size : 591 MB
	Command : merge_region '3d7ff56ac6289149c39ceef456e75466', '9b5e1ad85d3180c9051d03bfcd533089'
	 Region A : Name : 3d7ff56ac6289149c39ceef456e75466 Startkey : \x00\x00\x00\x04 EndKey : \x00\x00\x00\x05 Size : 273 MB
	 Region B : Name : 9b5e1ad85d3180c9051d03bfcd533089 Startkey : \x00\x00\x00\x05 EndKey : \x00\x00\x00\x06 Size : 20 MB

License
=======

Copyright © 2017 Cask Data, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
