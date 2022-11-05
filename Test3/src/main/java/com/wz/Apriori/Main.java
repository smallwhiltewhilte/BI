package com.wz.Apriori;

import java.util.*;
import java.io.*;

public class Main {
    static double min_support = 2;
    static double min_confident = 0.4;

    static String srcFilePath = System.getProperty("user.dir") + "/Test3/src/main/resources/data.txt";
    static String destinationFilePath = System.getProperty("user.dir") + "/Test3/src/main/resources/out.txt";
    private static FileWriter writer;

    static {
        try {
            writer = new FileWriter(destinationFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static ArrayList<ArrayList<String>> D = new ArrayList<ArrayList<String>>();
    static HashMap<ArrayList<String>, Integer> C = new HashMap<ArrayList<String>, Integer>();
    static HashMap<ArrayList<String>, Integer> L = new HashMap<ArrayList<String>, Integer>();

    static HashMap<ArrayList<String>, Integer> L_ALL = new HashMap<ArrayList<String>, Integer>();

    public Main() throws IOException {
    }


    public static ArrayList<ArrayList<String>> readTable(String filePath) throws IOException {
        ArrayList<ArrayList<String>> t = new ArrayList<ArrayList<String>>();
        ArrayList<String> t1 = null;
        File file = new File(filePath);
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(isr);
            String str = null;
            while ((str = bf.readLine()) != null) {
                t1 = new ArrayList<String>();
                String[] str1 = str.split(",");
                for (int i = 1; i < str1.length; i++) {
                    t1.add(str1[i]);
                }
                t.add(t1);
            }
            bf.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件不存在！");
        }
        writer.write("\n全集D:\n" + t+"\n");
        return t;
    }

    public static void pruning(HashMap<ArrayList<String>, Integer> C, HashMap<ArrayList<String>, Integer> L) {
        L.clear();

        L.putAll(C);

        ArrayList<ArrayList<String>> delete_key = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> key : L.keySet()) {
            if (L.get(key) < min_support) {
                delete_key.add(key);
            }
        }
        for (int i = 0; i < delete_key.size(); i++) {
            L.remove(delete_key.get(i));
        }
    }

    /**
     * 初始化事务数据库、项目集、候选集
     */
    public static void init() throws IOException {

        D = readTable(srcFilePath);

        for (int i = 0; i < D.size(); i++) {
            for (int j = 0; j < D.get(i).size(); j++) {
                String[] e = {D.get(i).get(j)};

                ArrayList<String> item = new ArrayList<String>(Arrays.asList(e));

                if (!C.containsKey(item)) {
                    C.put(item, 1);
                } else {
                    C.put(item, C.get(item) + 1);

                }
            }
        }

        pruning(C, L);

        L_ALL.putAll(L);
    }


    public static ArrayList<String> arrayListUnion(ArrayList<String> arraylist1, ArrayList<String> arraylist2) {
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.addAll(arraylist1);
        arraylist.addAll(arraylist2);

        arraylist = new ArrayList<String>(new HashSet<String>(arraylist));
        return arraylist;
    }


    public static HashMap<ArrayList<String>, Integer> iteration(HashMap<ArrayList<String>, Integer> C, HashMap<ArrayList<String>, Integer> L) throws IOException {
        HashMap<ArrayList<String>, Integer> L_temp = new HashMap<ArrayList<String>, Integer>();
        String str = null;
        int t = 1;
        while (L.size() > 0) {
            t++;
            L_temp.clear();
            L_temp.putAll(L);

            C.clear();

            ArrayList<ArrayList<String>> L_key = new ArrayList<ArrayList<String>>(L.keySet());
            for (int i = 0; i < L_key.size(); i++) {
                for (int j = i + 1; j < L_key.size(); j++) {
                    ArrayList<String> C_item = new ArrayList<String>();
                    C_item = new ArrayList<String>(arrayListUnion(L_key.get(i),
                            L_key.get(j)));
                    if (C_item.size() == t) {
                        C.put(C_item, 0);
                    }
                }
            }

            for (ArrayList<String> key : C.keySet()) {
                for (int i = 0; i < D.size(); i++) {
                    if (D.get(i).containsAll(key)) {
                        C.put(key, C.get(key) + 1);
                    }
                }
            }
            str = C.toString();
            writer.write("\n"+"候选" + t + "项集C: \n" + C+"\n");

            pruning(C, L);
            writer.write("\n"+"频繁" + t + "项集L: \n" + L + "\n");
            str = L.toString();

            L_ALL.putAll(L);
        }
        return L_temp;
    }

    public static ArrayList<ArrayList<String>> getSubset(ArrayList<String> L) {
        if (L.size() > 0) {
            ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
            for (int i = 0; i < Math.pow(2, L.size()); i++) {
                ArrayList<String> subSet = new ArrayList<String>();
                int index = i;
                for (int j = 0; j < L.size(); j++) {

                    if ((index & 1) == 1) {
                        subSet.add(L.get(j));
                    }
                    index >>= 1;
                }
                result.add(subSet);
            }
            return result;
        } else {
            return null;
        }
    }


    public static boolean intersectionIsNull(ArrayList<String> l1,
                                             ArrayList<String> l2) {
        Set<String> s1 = new HashSet<String>(l1);
        Set<String> s2 = new HashSet<String>(l2);

        s1.retainAll(s2);
        if (s1.size() > 0) {
            return false;
        } else {
            return true;
        }
    }


    public static void connection() throws IOException {
        for (ArrayList<String> key : L_ALL.keySet()) {
            ArrayList<ArrayList<String>> key_allSubset = getSubset(key);


            for (int i = 0; i < key_allSubset.size(); i++) {
                ArrayList<String> item_pre = key_allSubset.get(i);
                if (0 < item_pre.size() && item_pre.size() < key.size()) {

                    double item_pre_support = L_ALL.get(item_pre);

                    for (int j = 0; j < key_allSubset.size(); j++) {
                        ArrayList<String> item_post = key_allSubset.get(j);
                        if (0 < item_post.size()
                                && item_post.size() < key.size()
                                && arrayListUnion(item_pre, item_post).equals(key)
                                && intersectionIsNull(item_pre, item_post)) {
                            double d = L_ALL.get(arrayListUnion(item_pre, item_post));


                            double confident = d
                                    / item_pre_support;
                            if (confident > min_confident) {
                                writer.write(item_pre + "==>" + item_post);
                                writer.write("==>" + confident+"\n");
                            }
                        }

                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Main apriori = new Main();
        init();
        writer.write("\n"+"候选1项集C：\n" + C+"\n");
        writer.write("\n"+"频繁1项集L：\n" + L + "\n");
        L = iteration(C, L);
        writer.write("\n"+"关联规则如下:"+"\n");
        connection();
        writer.close();
    }
}