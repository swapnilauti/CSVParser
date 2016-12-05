<h1><b>Overview</b></h1>
 
In the ordinary database systems, querying a CSV file needs it to be parsed and indexed before
any query could be executed. As a consequence, a lot of time is incurred in the initial loading,
type checking, type conversions and indexing before the first query could be executed.
In this project, we aim to build different parsing techniques which would enable us to
dynamically parse the CSV file as and when the query is encountered. We also aim to reduce
the time taken to execute the first query on the CSV file. By deferring the parsing, we also save
time by parsing only on the columns/lookup values of interest. In other words, data which is not
requested by any query would never be parsed.

At any given time, when a query on an untouched data in the CSV file is encountered, we aim to
dynamically parse this new data. For that purpose, we build a data structure called Positional
Maps which is a mapping from the location in the CSV file to the value. The column fetching or
the value lookup is done with the help of this positional maps [2]. Positional maps are twodimensional arrays which help in retrieving the desired value from a block read of the file.
During the initial file read, as we scan all the bytes, we simultaneously create this positional
maps for every value in the CSV file, which would aid us in locating any cell within the file, thus
significantly reducing the time taken to parse data for the subsequent queries.

The data value fetching strategy is coupled with Just-In-Time data structures (JITDs) [1] where
we aim to dynamically index the column based on the workload. The physical representation of
a JITD is defined by a set of generic components, or cogs, which capture the structure and
semantics of the representation. The advantage of using JITDs is that a single JITD may
implement and alternate between many different policies, rapidly adapting its behavior to
fluctuating workload demands.

By combining JITDs with positional maps, we can create a prototype of CSV database which
can effectively load and parse the file on demand as well as index the data on-the-fly. This
combination can be effective for the read-only workloads.

<h1><b>Problem Statement</b></h1>

In traditional Databases the initial indexing of the data takes a lot of time, hence the data is not
ready for querying immediately. There can be scenarios where the analytics on the data has to
be run as soon as the data is ingested in the database. In order to make the data available for
querying we can parse and index the data on-the-fly while processing the query. The aim of our
project is to compare different parsing techniques and determine which technique would be
better for a given scenario.

<h1><b>Implementation Details</b></h1>

We have implemented our programs in Java and have considered three parsing techniques to
parse the CSV files:

<b>Naive Parser:</b> Naive parser is a prototype of traditional DB where all the data is indexed in the
start. Our implementation of the Naive Parser includes reading the entire CSV file upfront into
the memory and then parse each cell and store it in a two dimensional array.

<b>InFile Parser:</b> InFile parser reads the file block by block into the memory. During the first query
it creates a positional map for the entire CSV file and returns the data requested in the query.
The subsequent queries make use of the positional map to map to the desired data value in the
file. The block size can be controlled and can be tuned for good performance.

<b>InMem Parser:</b> InMem parser reads the file initially and stores it in memory in a byte array.
Then whenever first query comes it creates a positional map and return the required data value.
On subsequent queries, it uses the byte array and positional map to return the required data.
The main difference between InMem and InFile is that InFile doesn’t keep the file content in
memory and only fetches value from the file via block reads.

In all the techniques discussed above, we have read the file in bytes so as to defer the casting
lazily to the time when the values are required. Also for casting we have implemented a basic
function to reduce the time used in extra checks in default casting provided by java libraries.

<h1><b>Metrics for success</b></h1>

We are comparing the three parsing techniques i.e., InMem Parser, InFile Parser and Naïve
Parser based on the following parameters:
1. Total time taken to load the whole CSV file
2. Parsing time for the first query
3. Maximum file size supported
