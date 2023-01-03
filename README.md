# SimpleLogMerger
Merges .log Files in an simple naive way.

># Attention!
- The Log Merger is only tested with Java 8.
- It doesn't unzip zip compressed Files like log.gz or something.
- It can set the modification Date of a log File by it's Name (like "2023-01-03-1.log").
- The current Name modification Date format is 'yyyy-MM-dd-HH' which stands for 'year-month-day-hour'.
- The modification Date is used for the sorting inside the merged log File.
- All log Files named "latest.log" have a prioritised sorting.
