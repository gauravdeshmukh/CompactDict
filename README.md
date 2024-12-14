# CompactDict
A learning project to create library for compact in-memory dictionary in Java to understand the different ways to represent dictionary and its trade-offs.


It has following dictionary implementations comapred:
1. **HashMap**: Uses Java's standard HashMap to store key-value pairs.
2. **DereferencedHashMap**: When cardinality of values is very less compared to cardinality of keys, we don't need to store the values repeatedly for each key. Instead, we can store value once and store its reference against the key in the HashMap.
3. **Trie**: Trie optimizes the storage of keys by reusing the prefixes. We have to travers the key character by character to reach to the value.
4. **DereferencedTrie**: Similar to DereferencedHashMap, it avoids storing the value repeatedly.
5. **CompiledTrie**: After all key-value pairs are added to dictionary, a compile() method is called. It represent the trie into a compact byte array, thereby optimizing the storage of trie.
6. **ValuePrefixedCompiledTrie**: Instead of storing values at the leaf nodes, it uses the strategy of Finite State Transducers (FST) to store the value prefixes on the edges. As the trie is traversed character by character in key, the values on the edges are appended to get the final value. Instead of dereferencing separately, it uses the value prefix strategy similar to key prefix strategy of trie to optimize the storage required for values.
7. **FST (FiniteStateTransducer)**: This creates actual FST out of trie, when compile method is called. Initially, all key-value pairs are added to trie. Then while serializing to byte array, it checks if the suffix nodes are common. This ensures that similar nodes in trie are stored only once. Thus storage is optimized not only based on the key prefixes, but also on the key suffixes.

For a data of 25,000 key value pairs where keys are postal code to postal code pairs mapping to one of the 4 possible values, here is the comparison of different implementations (Raw csv file: 1.278MB):

| Type                                   | Disk file size | Put time (ms) | Get time (ms)  | % Compression |
| -------------------------------------- | -------------- | -------- | -------- | ------------- |
| HashMapDict                            | 2.029MB        | 19       | 10       | -58.8         |
| DereferencedHashMapDict                | 1.553MB        | 8        | 5        | -21.5         |
| BasicTrieDict                          | 1.742MB        | 27       | 17       | -36.3         |
| DereferencedTrieDict                   | 1.265MB        | 29       | 12       | -21.5         |
| CompiledTrieDict (dereferenced values) | 0.235MB        | 47       | 14       | 81.6          |
| ValuePrefixedCompiledTrieDict          | 0.345MB        | 200      | 60       | 72.9          |
| FST                                    | 0.082MB        | 170      | 60       | 93.6          |
| [Lucene FST](https://github.com/apache/lucene)| 0.199MB        |          |          | 84.4          |

*Note: Put and Get time is to insert and query all 25,000 keys* 

### Summary
1. As expected HashMap is the fastest to query, but takes the maximum space, even larger than the raw file
2. Dereferencing values saves good amount of space
3. Simply converting the default java representation of objects into a compact byte array representation saves lot of space. Of course, it impacts the query time.
4. Finite State Transducers (FST) gives the maximum compression and give the query performance good enough for most of the real world applications. 

#### Why FST implementation in this library achives more compression than Lucene's FST 
Implementation in this library, first creates trie out of all the keys and then creates the minimal FST out of it. However, this intermediate state of creating full trie requires lot of memory. For the millions of key-value pairs, such trie does not fit into the memory. Thus this FST implementation is not practically scalable for the real-wrold applications. 

Lucene uses algorithm from the paper *Direct Construction of Minimal Acyclic Subsequential Transducers by Stoyan Mihov and Denis Maurel*. This requires that keys are inserted in a sorted order. By exploiting pre-sorting of keys, it progressively creates the FST such that memory requirement during FST creation is minimal. However, this can miss optimizing on some of the common suffixes, thus ending up storing those suffixes multiple times. This is an acceptable trade-off, as resulting FST is still high compressed.

#### Why I am not implementing the similar FST as Lucene
This is only a learning project. I had already read the Lucene's FST implementation. I just wanted to try creating the FST from scratch to understand it better and to get my hands dirty at byte serialization of data. In the process, I have understood it better how Lucene FST is achieving such high compression ration and its implementation decisions better. Re-implementing the same code again is no more exciting.