package com.mu.smartdiffer;
//import org.apache.lucene.queryparser.classic.ParseException;


import java.io.*;
import java.text.ParseException;
import java.util.*;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
/**
 * Created by arulsmv on 2/12/17.
 */
public class Gitdiffer {
    public Gitdiffer() {
    }
    public static void main(String[] args) {
        Gitdiffer differ = new Gitdiffer();
        boolean success = true;
        ArgumentParser argumentParser = ArgumentParsers.newFor("differ")
                .build().defaultHelp(true)
                .description("Differ");
        argumentParser.addArgument("-f", "--from")
                .help("From commit id");
        argumentParser.addArgument("-t", "--to")
                .help("To commit id");
        argumentParser.addArgument("-o1","--out1" )
                .help("output from file");
        argumentParser.addArgument("-o2","--out2" )
                .help("output to file");
        Namespace ns = null;
        try {
            ns = argumentParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argumentParser.handleError(e);
            System.exit(1);
        }

        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec("git diff " + ns.getString("from")
                    + " " + ns.getString("to"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);

        List<String> output = new ArrayList<String>();
        Map<String, List<Integer>> fileLineMap1 = new HashMap<String, List<Integer>>();
        Map<String, List<Integer>> fileLineMap2 = new HashMap<String, List<Integer>>();

        try {
            String line = null;
            while ( (line = br.readLine()) != null){
                output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int exitval= proc.waitFor();
            String file1 = "";
            String file2 = "";
            List<Integer> linelist1;
            List<Integer> linelist2;
            linelist1 = new ArrayList<Integer>();
            linelist2= new ArrayList<Integer>();
            for (String line : output) {
                if (line.startsWith("---")) {
                    String [] parts = line.split(" ");
                    assert parts.length == 2;
                    if (file1.length() > 0)
                        fileLineMap1.put(file1, linelist1);
                    file1 = parts[1].replaceFirst("a/" ,"");
                    linelist1 = new ArrayList<Integer>();
                }
                if( line.startsWith("+++")) {
                    String [] parts = line.split(" ");
                    assert parts.length == 2;
                    if (file2.length() > 0)
                        fileLineMap2.put(file2, linelist2);
                    file2 = parts[1].replaceFirst("b/" ,"");
                    linelist2= new ArrayList<Integer>();
                }
                if (line.startsWith("@@")) {
                    String[] parts = line.split(" ");
                    assert parts.length > 3;
                    String[] lnums1 = parts[1].replaceFirst("-", "").split(",");
                    String[] lnums2 = parts[2].replaceFirst("\\+", "").split(",");
                    assert lnums1.length == 2;
                    assert lnums2.length == 2;
                    Integer fromLine = Integer.parseInt(lnums1[0]);
                    Integer numberOfLines = Integer.parseInt(lnums1[1]);
                    for(int i = 0; i <= numberOfLines; i++) {
                        linelist1.add(fromLine+i);
                    }
                    fromLine = Integer.parseInt(lnums2[0]);
                    numberOfLines = Integer.parseInt(lnums2[1]);
                    for(int i = 0; i <= numberOfLines; i++) {
                        linelist2.add(fromLine+i);
                    }
                }
            }
            fileLineMap1.put(file1, linelist1);
            fileLineMap2.put(file2, linelist2);
            emitToFile(ns.getString("out1"), fileLineMap1);
            emitToFile(ns.getString("out2"), fileLineMap2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void emitToFile(String outfile, Map<String, List<Integer>> fileLineMap) {
        try {
            BufferedWriter bw  = new BufferedWriter( new OutputStreamWriter (new FileOutputStream(outfile)));
            for (Map.Entry<String, List<Integer>> entry : fileLineMap.entrySet()) {
                if (!entry.getKey().startsWith("/")) {
                    bw.write("/");
                }
                bw.write(entry.getKey());
                bw.write("\t");
                for(Integer i: entry.getValue()) {
                    bw.write(i.toString());
                    bw.write(",");
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
