<h1><b>Overview</b></h1>
 
In the ordinary database systems, querying a CSV file needs the file to be parsed and indexed before any query could be executed. As a consequence, lot of time is incurred in the initial loading, type checking, type conversions and indexing before the first query could be executed.
In this project, we aim to create indexes on the fly. i.e. when a query on a new column is encountered, we index only that column. We also aim to reduce the time taken to execute the first query on the CSV file. By deferring the index creation to query time, we also save time by creating indexes only on the columns of interest(the columns on which query is done). A column on which we never get a query will never be indexed.
 
The column fetching is done with the help of positional maps[2]. Positional maps are two dimensional arrays which helps in retrieving the desired column from a block read of the file. During the initial file read, as we scan all the bytes, we simultaneously create mapping for every value in the CSV file, which could aid us in mapping to any cell within the file, thus reducing the time taken to fetch subsequent columns considerably.

This column fetching strategy is coupled with Just-In-Time data structures (JITDs)[1] where we aim to dynamically index the column based on the workload. The physical representation of a JITD is defined by a set of generic components, or cogs, which capture the structure and semantics of the representation. Advantage of using JITDs is that a single JITD may implement and alternate between many different policies, rapidly adapting its behavior to fluctuating workload demands. 

By combining JITDs with positional maps, we can create a prototype of CSV database which can effectively load and parse the file on demand as well as index the data on fly. This combination can be effective for the read only workloads in which the insertions are in batches.   


<h1><b>Problem Statement</b></h1>

In traditional Databases the initial indexing of the data takes a lot of time, hence the data is not ready for querying immediately. There can be scenarios where the analytics on the data has to be run as soon as the data is ingested in the database. In order to make the data available for query we can index the data on the fly while processing the query. The aim of our project is to make a prototype of database which can index the various columns of the CSV file while running queries on them. This will help us in amortizing the cost of initial read and parsing of the file over the time taken for querying. Our database will be optimized to handle read only queries on large CSV files where reading the complete file and keeping it In-Memory is not feasible. 
Implementation Details

We have implemented our programs in Java and have taken three approaches to read the CSV files:
 
<b>Naive Parser:</b> Naive parser is a prototype of traditional DB where all the data is indexed in the start. For this we implemented a Naive Parser which would read the file completely in the memory and then parse each cell and store it in a two dimensional array.
 
<b>In-File Parser:</b> In-File parser reads the file block by block into the memory. During the first query it makes a positional map for the whole file and returns the required column. Any subsequent query fetch blocks from file the uses the positional map to get the desired column values from the block and return the queried column. The block size can be controlled and can be tuned for good performance.
 
<b>In-Mem Parser:</b> In-Mem parser reads the file initially and stores it in memory in a byte array. Then whenever first query comes it creates a positional map and return the required column. On subsequent queries, it uses the byte array and positional map to return the required columns. The main difference between In-Mem and In-File is that In-File doesn’t keep the file content in memory and only fetches value from the file via block reads.
 
In all the approaches we have read the file in bytes so as to defer the casting lazily to the time when the values are required. Also for casting we have implemented a basic function to reduce the time used in extra checks in default casting provided by java libraries.
 
There were cases when we had some unusual readings because of the Garbage Collector kicking in. To avoid its effect on our experiments, we have explicitly called garbage collector in places where it won’t affect the reading time.
 
 

<h1><b>Metrics for success</b></h1>

In-File is the approach we are considering to use for our workload and scenario. Our metrics of success will be its comparison with two other approaches we are using i.e, Naive approach and In-Mem approach. Instead of Naive approach we could have used some other library CSV parsers but due to error handling and other type checking, its performance won’t be a good comparison to our approach. Naive parser would represent a classical approach where all the indexing and the casting of data is done upfront. In-Mem approach is very much similar to our approach, the only difference being the whole content of file being present in memory. We will show that for big files In-Mem approach will not be possible and for small files In-File approach will be comparable to In-Mem approach. Also naive approach will not be ideal for large files and the time taken for Naive Parse will also be comparable to In-File approach.
