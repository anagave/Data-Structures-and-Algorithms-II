// Query 4:
// The objective of this query is to build a Directed Acyclic Graph, with buyer Ids as nodes and the transaction between the 2 buyers as an edge, 
// and the price of an NFT as the weight of the edge. The output is printed in the form of an adjacency list.
// Key data structures: HashSet, Array List, TreeMap, HashMap.
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
class Node {
    private String name;
    private LinkedList < Edge > edgeList;
    public Node(String name) {
        this.name = name;
        edgeList = new LinkedList < > ();
    }
    public String getName() {
        return name;
    }
    public LinkedList < Edge > getEdges() {
        return edgeList;
    }
}
class Edge {
    private String weight;
    private Node destVertex;
    public Edge(Node dest, String w) {
        this.destVertex = dest;
        this.weight = w;
    }
    public Edge(Node dest) {
        this.destVertex = dest;
    }
    public String getWeight() {
        return weight;
    }
    public Node getDestVertex() {
        return destVertex;
    }
}
class Graph {
    private HashSet < Node > nodes;
    public Graph() throws IOException {
        nodes = new HashSet < > ();
    }
    public boolean AddEdge(Node v1, Node v2, String weight) {
        return v1.getEdges().add(new Edge(v2, weight));
    }
    public boolean AddVertex(Node v) {
        return nodes.add(v);
    }
    public void DFSUtil(String keys,TreeMap<String, List<String>> mapdfs, HashSet<String> booleanSet ) throws IOException
    {
            if(booleanSet.contains(keys)){
                //System.out.println(keys + "\n");
            }
            Iterator<String> str = mapdfs.get(keys).listIterator();
            while(str.hasNext()){
                String n = str.next();
                if(!booleanSet.contains(n)){
                    booleanSet.add(n);
                    DFSUtil(n,mapdfs, booleanSet);
                }
            }
    }
    public void DFS(TreeMap<String, List<String>> mapdfs) throws IOException
    {
        HashSet<String> booleanSet = new HashSet<>();
        for(Map.Entry<String,List<String>> entry : mapdfs.entrySet()) {
            String keys = entry.getKey();
            if(!booleanSet.contains(keys)){
                booleanSet.add(keys);
                DFSUtil(keys,mapdfs, booleanSet);
            }
        }
    }
    public TreeMap<String, List<String>> getTreeMap(Map<String, Integer> matrixPosMap, int size) throws IOException {
            TreeMap<String, List<String>> mapDFS = new TreeMap<String, List<String>>();
            BufferedWriter output = new BufferedWriter(new FileWriter("OriginalMatrix.txt", true));
            for (Node v: nodes) {
                int c = 0;
                List<String> in = new ArrayList<String>(Collections.nCopies(size, "0"));
                for (Edge e: v.getEdges()) {
                    //outputMatrix[matrixPosMap.get(v.getName())][matrixPosMap.get(e.getDestVertex().getName())] = e.getWeight();
                    in.add(matrixPosMap.get(e.getDestVertex().getName()), e.getWeight());
                    if(e.getWeight() != "0") {
                        mapDFS.computeIfAbsent(v.getName(), key -> new ArrayList<>()).add(e.getDestVertex().getName());
                        c++;
                    }
                }
                if(c==0) {
                    List<String> empt = new ArrayList<>();
                    mapDFS.put(v.getName(), empt);
                }
                output.write("[");
                for (int k = 0; k < in.size(); k++) {
                    output.write(in.get(k) + ",");
                }
                output.write("]");
                output.newLine();
            }
            output.close();
            return mapDFS;     
    }
    
}
public class Main{
    static void ExConstructor() throws FileNotFoundException 
    {
        try {
            Graph ourGraph = new Graph();
            Map<String,List<String>> map = new TreeMap<>();
            Set<String> setBuyers = new LinkedHashSet<String>();
            Scanner sc = new Scanner(System.in);
            System.out.print("Input File Name: ");
            String pathname = sc.next();
            HashMap<String, Double> currencyConvert = new HashMap<String, Double>();
            currencyConvert.put("ETH", 1309.97);
            currencyConvert.put("WETH", 1322.16);
            currencyConvert.put("ASH", 0.9406);
            currencyConvert.put("GALA", 0.03748);
            currencyConvert.put("TATR", 0.012056);
            currencyConvert.put("USDC", 1.00);
            currencyConvert.put("MANA", 0.64205);
            currencyConvert.put("SAND", 0.7919);
            currencyConvert.put("RARI", 2.18);
            currencyConvert.put("CTZN", 0.00321);
            currencyConvert.put("APE", 4.62);
            Double res;
            String line;
            BufferedReader br;
            br = new BufferedReader(new FileReader(pathname));
            line = br.readLine();
            while((line = br.readLine()) != null) {
                String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -12);
                String p = s[9].replace('"',' ');
                p = p.replace(",","");
                String[] price = p.split(" ");
                setBuyers.add(s[4]+"," +s[1]);
                if(price.length >= 3) {
                    if(currencyConvert.containsKey(String.valueOf(price[2]))) {
                        res = currencyConvert.get(String.valueOf(price[2])) * Double.parseDouble(price[1]);
                        String l = s[4] + "," + s[1] + ", "+ '"' + String.valueOf(res) + '"';
                        map.computeIfAbsent(s[6], key -> new ArrayList<>()).add(l);
                    }
                }
                else { 
                    String l = s[4] + "," + s[1] + "," + s[9];
                    map.computeIfAbsent(s[6], key -> new ArrayList<>()).add(l);
                }
            }
            br.close();
            Map<String, Integer> matrixPosMap = new HashMap<>();
            int c = 0;
            Iterator<String> itr = setBuyers.iterator();
            while(itr.hasNext()){
                matrixPosMap.put(itr.next(),c);
                c++;
            }
            for(Map.Entry<String,List<String>> entry : map.entrySet()) {   
                List<String> valueList = entry.getValue(); 
                for (int i = 0; i < valueList.size(); i++) {
                    if(i<valueList.size()-1) {
                        String[] A = valueList.get(i).split(",");
                        Node v0 = new Node(A[0] + "," + A[1]);
                        ourGraph.AddVertex(v0);
                        String[] B = valueList.get(i+1).split(",");
                        Node v1 = new Node(B[0] + "," + B[1]);
                        ourGraph.AddVertex(v1);
                        ourGraph.AddEdge(v0, v1, B[2]);
                    }     
                }   
            }
            TreeMap<String, List<String>> mapdfs=ourGraph.getTreeMap(matrixPosMap, setBuyers.size());
            ourGraph.DFS(mapdfs);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        try {
            long startTime = System.nanoTime(); 
            ExConstructor();
            long stopTime = System.nanoTime();
            System.out.println((stopTime - startTime)/1e-9);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}