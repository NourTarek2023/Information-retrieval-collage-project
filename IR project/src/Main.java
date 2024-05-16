import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Main {

    static class Pair<Key> implements Comparable<Pair<Key>> {

        public Key key;
        public Double val;

        public Pair(Key key, Double val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public int compareTo(Pair<Key> pair) {
            if (this.val > pair.val) {
                return -1;
            } else if (this.val < pair.val) {
                return 1;
            }
            return 0;
        }
    }


    static ArrayList<String> wordsFile1 = process("file1.txt");
    static ArrayList<String> wordsFile2 = process("file2.txt");
    static ArrayList<String> wordsFile3 = process("file3.txt");
    static ArrayList<String> wordsFile4 = process("file4.txt");


    public static String getFileContent(String fileName) {
        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                line = line.replace("\n", "");
                fileContent.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent.toString();
    }

    public static ArrayList<String> stringToWords(String content) {
        String[] wordsArray = content.split(" ");


        ArrayList<String> words = new ArrayList<>();

        for (String word : wordsArray) {
            words.add(word.toLowerCase());
        }
        return words;
    }

    public static ArrayList<String> removeStopWords(ArrayList<String> words) {

        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> stopWords = stringToWords(getFileContent("stopwords.txt"));
        for (String word : words) {
            if (!stopWords.contains(word))
                result.add(word);
        }

        return result;
    }

    public static ArrayList<String> process(String file) {
        String filename = getFileContent(file);
        ArrayList<String> words = removeStopWords(stringToWords(filename));
        return words;
    }

    public static double redundant(ArrayList<String> file, String[] query) {
        double result = 0;
        for (String word : query) {
            int count = 0;
            for (String wordOfFile : file) {
                if (wordOfFile.equals(word)) {
                    count++;
                }
            }
            result += (double) count / file.size();
        }
        return result;
    }


    public static void statisticalSearch(String QueryString) {

        String[] Query = QueryString.toLowerCase().split(" ");
        Pair[] rank = new Pair[4];

        rank[0] = new Pair(1, redundant(wordsFile1, Query));
        rank[1] = new Pair(2, redundant(wordsFile2, Query));
        rank[2] = new Pair(3, redundant(wordsFile3, Query));
        rank[3] = new Pair(4, redundant(wordsFile4, Query));

        Arrays.sort(rank);

        for (var p : rank) {
            System.out.println("Document: " + p.key + " , Rank = " + p.val);
        }
    }

    public static ArrayList<String> searchWord(String word) {
        ArrayList<String> foundDoc = new ArrayList<>();

        if (wordsFile1.contains(word)) {
            foundDoc.add("Doc 1");
        }
        if (wordsFile2.contains(word)) {
            foundDoc.add("Doc 2");
        }
        if (wordsFile3.contains(word)) {
            foundDoc.add("Doc 3");
        }
        if (wordsFile4.contains(word)) {
            foundDoc.add("Doc 4");
        }

        return foundDoc;
    }


    public static void and(String a, String b) {
        ArrayList<String> B = searchWord(b);
        ArrayList<String> A = searchWord(a);
        ArrayList<String> C = new ArrayList<>();

        for (int i1 = 0; i1 < B.size(); i1++) {
            for (int i2 = 0; i2 < A.size(); i2++) {
                if (A.get(i2).equals(B.get(i1)))
                    C.add(A.get(i2));
            }
        }
        if (C.isEmpty())
            System.out.println("No Document found !");
        else
            System.out.println(C);
    }

    public static void or(String a, String b) {
        ArrayList<String> B = searchWord(b);
        ArrayList<String> A = searchWord(a);

        for (int i1 = 0; i1 < B.size(); i1++) {
            if (!A.contains(B.get(i1))) {
                A.add(B.get(i1));
            }
        }
        if (A.isEmpty()) System.out.println("No Document found !");
        else System.out.println(A);
    }

    public static void not(String a, String b) {
        ArrayList<String> B = searchWord(b);
        ArrayList<String> A = searchWord(a);

        for (int i1 = 0; i1 < B.size(); i1++) {
            for (int i2 = 0; i2 < A.size(); i2++) {
                if (A.get(i2) == B.get(i1))
                    A.remove(i2);
            }
        }
        if (A.isEmpty())
            System.out.println("No Document found !");
        else System.out.println(A);
        ;
    }

    public static void booleanSearch(String QueryString) {
        String[] Query = QueryString.toLowerCase().split(" ");

        if (Query.length == 1) {
            if (!searchWord(Query[0]).isEmpty())
                System.out.println(searchWord(Query[0]));
            else
                System.out.println("No document found");
        } else {

            for (int i = 0; i < Query.length; i++) {
                if (Query[i].equals("or")) or(Query[i - 1], Query[i + 1]);
                else if (Query[i].equals("and")) and(Query[i - 1], Query[i + 1]);
                else if (Query[i].equals("not")) not(Query[i - 1], Query[i + 1]);
            }

        }

    }


    public static ArrayList<String> uniqueWords(ArrayList<String> doc, String[] query) {
        ArrayList<String> result = new ArrayList<>();

        for (String word : doc) {
            if (!result.contains(word))
                result.add(word);
        }

        for (String word : query) {
            if (!result.contains(word))
                result.add(word);
        }
        return result;
    }


    public static double cosineSimilarity(ArrayList<String> document, String QueryString) {
        String[] Query = QueryString.toLowerCase().split(" ");
        ArrayList<Integer> vD = new ArrayList<>();
        ArrayList<Integer> vQ = new ArrayList<>();
        ArrayList<String> unionWords = uniqueWords(document, Query);

        double magvD = 0, magvQ = 0, dotProduct = 0;

        for (String word : unionWords) {
            if (document.contains(word)) {
                vD.add(1);
                magvD++;
            } else vD.add(0);
        }

        for (String word : unionWords) {
            boolean found = false;
            for (int i = 0; i < Query.length; i++) {
                if (Query[i].equals(word)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                vQ.add(1);
                magvQ++;
            } else vQ.add(0);
        }

        magvD = Math.sqrt(magvD);
        magvQ = Math.sqrt(magvQ);

        for (int i = 0; i < vD.size() - 1; i++) {
            dotProduct += vD.get(i) * vQ.get(i);
        }
        return dotProduct / (magvD * magvQ);
    }

    public static double jaccardMethod(ArrayList<String> document, String QueryString) {
        String[] Query = QueryString.toLowerCase().split(" ");
        ArrayList<String> unions = uniqueWords(document, Query);
        ArrayList<String> intersection = new ArrayList<>();

        for (int i = 0; i < Query.length; i++) {
            if (document.contains(Query[i])) {
                if (!intersection.contains(Query[i]))
                    intersection.add(Query[i]);
            }
        }

        System.out.println(unions);
        System.out.println(intersection);


        return (double) intersection.size() / unions.size();

    }


    public static void main(String[] args) {

//        booleanSearch("A AND B");
//        booleanSearch("c or B");
//        booleanSearch("A Not c");
//
        statisticalSearch("A B D");

        System.out.println(cosineSimilarity(wordsFile1,"Ziko Quick brown fox "));

        System.out.println(jaccardMethod(wordsFile2,"information retrieval"));
    }


}