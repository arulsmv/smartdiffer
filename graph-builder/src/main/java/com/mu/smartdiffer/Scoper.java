/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 */
package com.mu.smartdiffer;

import java.io.*;
import java.text.ParseException;
//import org.apache.lucene.queryparser.classic.ParseException;
import java.util.*;

import org.opensolaris.opengrok.configuration.RuntimeEnvironment;
import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexableField;
import org.opensolaris.opengrok.analysis.Scopes;
import org.opensolaris.opengrok.index.IndexDatabase;
import org.opensolaris.opengrok.search.Hit;
import org.opensolaris.opengrok.search.QueryBuilder;
import org.opensolaris.opengrok.search.SearchEngine;
import org.opensolaris.opengrok.util.Getopt;

/**
 * Search and list the matching files
 */
@SuppressWarnings({"PMD.AvoidPrintStackTrace", "PMD.SystemPrintln"})
final class     Scoper {

    private static final String usage = "USAGE: Search -R <configuration.xml>  -i  <Input Diff file> -o <Output Graph File>..\n" +
            "\t -R <configuration.xml> Read configuration from the specified file\n" +
            "\t -i <input diff file>" +
            "\t -o <output graph file>" +
            "\t -v1 [version1]" +
            "\t -v2 [version2]\n";

    //private static int MAX_LINE_NO = 10000;
    private SearchEngine engine;
    final HashMap<String, Set<Scopes.Scope>> scopesMap = new HashMap();
    final HashMap<String, List<Integer>> srcFilesLineMap = new HashMap();
    final List<ReferenceEntry> entries = new ArrayList();
    private String conf;

    @SuppressWarnings({"PMD.SwitchStmtsShouldHaveDefault"})
    protected boolean parseCmdLine(String[] argv) {
        String inputFile = null;
        String outputFile = null;
        engine = new SearchEngine();
        Getopt getopt = new Getopt(argv, "R:i:o:");
        try {
            getopt.parse();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.println(usage);
            return false;
        }

        int cmd;
        while ((cmd = getopt.getOpt()) != -1) {
            switch (cmd) {
                case 'R':
                    try {
                        RuntimeEnvironment.getInstance().readConfiguration(new File(conf=getopt.getOptarg()));
                    } catch (IOException e) {
                        System.err.println("Failed to read config file: ");
                        System.err.println(e.getMessage());
                        return false;
                    }
                    break;
                case 'i':
                    inputFile=getopt.getOptarg();
                    intilizeSourceFileLineMap(inputFile);
                    break;
                case 'o':
                    outputFile=getopt.getOptarg();
                    getScopesMap();
                    getRefereces();
                    printScopes(outputFile);
                    break;

            }
        }

        return true;
    }

    private void printScopes(String outputFile){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
            for(ReferenceEntry entry:entries) {
                bw.write(entry.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getRefereces(){
        final List<Hit> results = new ArrayList();
        int nhits;
        int totalResults;
        try {
            for (Map.Entry<String, Set<Scopes.Scope>> sEntry : scopesMap.entrySet()) {
                for (Scopes.Scope scope : sEntry.getValue()) {
                    String symbol = scope.getName();
                    engine.setSymbol(symbol);
                    results.clear();
                    nhits = engine.search();
                    System.out.println("Searching  " + symbol + " hits " + nhits);

                    engine.results(0, nhits, results);
                    for(Hit hit:results) {
                        int lineno;
                        String refFileName = hit.getFilename();
                        String refFilePath = hit.getPath().replaceFirst("/", "");
                        if (!scopesMap.containsKey(refFilePath)) {
                            System.out.println("Skipping file " + refFileName);
                            continue;
                        }
                        //else {
                        System.out.println("found scoping " + refFilePath);
                        //}
                        try {
                             lineno = Integer.parseInt(hit.getLineno());
                        } catch (NumberFormatException e) {
                            System.out.println("Error in parsing int for file " + refFilePath);
                            continue;
                        }
                        // Second Iteration
                        for (Scopes.Scope refferedScope : scopesMap.get(refFilePath)){
                            if( refFilePath.equals(sEntry.getKey())) {
                                // System.out.println("Same File");
                                if (refferedScope.getLineFrom() == lineno) {
                                   // System.out.println("Skippling the symbol  " + symbol);
                                    continue;
                                }
                            }
                            if (lineno >= refferedScope.getLineFrom() && lineno <= refferedScope.getLineTo()) {
                                ReferenceEntry entry = new ReferenceEntry(refferedScope.getName(),
                                        refFilePath, lineno, symbol,sEntry.getKey(),
                                        refferedScope.getLineFrom());
                                entries.add(entry);
                                //System.out.println("Call is made from function "
                                //        + refferedScope.getName() + " at "  + refFileName + ":" + lineno +" to " + symbol + "@" +sEntry.getKey());

                            }
                        }
                    }
                    if(scope.getSignature()!=null) {
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getScopesMap() {
        try {
            for(Map.Entry<String, List<Integer>> entry: srcFilesLineMap.entrySet()) {
                Set<Scopes.Scope> methodScopes = new HashSet();
                String sfile = entry.getKey();
                try {
                    Scopes scopes = getScopes(sfile);
                        if (scopes != null) {
                            for(Integer i: entry.getValue()){
                                Scopes.Scope scope = scopes.getScope(i.intValue());
                                methodScopes.add(scope);
                            }
                        }
                         else {
                        System.out.println("Error in getting scope for " + sfile );
                        System.exit(1);
                    }
                   // }
                    //else {
                    System.out.println("Error in scoping size" + methodScopes.size());
                    //}
                    scopesMap.put(sfile,methodScopes);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (org.apache.lucene.queryparser.classic.ParseException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void intilizeSourceFileLineMap(String inputFile) {
        String line;
        String[] parts; // = new String[2];
        String[] lines; // = new String[MAX_LINE_NO];
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
            while((line = bf.readLine()) != null) {
                parts = line.split("\t");
                lines = parts[1].split(",");
                List<Integer> lineList = new ArrayList();
                for(String l:lines) {
                    lineList.add(Integer.parseInt(l));
                }
                srcFilesLineMap.put(parts[0],lineList);
                //else {
                System.out.println("lines in scoping " + lineList.size() + " for " + parts[0]);
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Scopes getScopes(String srcPath)
            throws IOException, ParseException, ClassNotFoundException, org.apache.lucene.queryparser.classic.ParseException {

        IndexReader ireader = IndexDatabase.getIndexReader(srcPath);
        Scopes scopes = null;
        if (ireader == null) {
            // No index, no definitions...
            System.out.println("No indexreader");
            return null;
        }
        Query q = new QueryBuilder().setPath(srcPath).build();
        IndexSearcher searcher = new IndexSearcher(ireader);
        TopDocs top = searcher.search(q, 1);
        if (top.totalHits == 0) {
            // No hits, no definitions...
            return null;
        }
        System.out.printf("total hits are %d \n", top.totalHits);
        Document doc = searcher.doc(top.scoreDocs[0].doc);
        String foundPath = doc.get(QueryBuilder.PATH);
        System.out.printf("found path is %s\n" , foundPath);
        // Only use the Scope if we found an exact match.
        if (srcPath.equals(foundPath) || srcPath.equals("/" + foundPath) || foundPath.equals("/" +srcPath)) {
            IndexableField scop = doc.getField(QueryBuilder.SCOPES);
            if (scop != null) {
                 scopes = Scopes.deserialize(scop.binaryValue().bytes);
            }
        } else {
            System.out.println("error in found path");
        }
        ireader.close();
        return scopes;
    }

    /**
     * usage Search index "query" prune path
     * @param argv command line arguments
     */
    public static void main(String[] argv) {
        Scoper scoper = new Scoper();
        boolean success = true;
        if (scoper.parseCmdLine(argv)){
            System.out.println("Done... Thank you");

        }

        if (!success) {
            System.exit(1);
        }
    }
}
