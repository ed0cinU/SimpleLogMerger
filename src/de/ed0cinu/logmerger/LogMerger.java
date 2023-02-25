package de.ed0cinu.logmerger;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 *   Copyright (c) 2023 ed0cinU
 */

public final class LogMerger {

    private static final List<File> files = new ArrayList<>(), afterOtherFiles = new ArrayList<>();

    private static final String latestLog = "latest.log";

    private static Date getDate(final String date, final String dateFormat) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        dateFormatter.setLenient(false);
        try {
            return dateFormatter.parse(date.trim());
        }
        catch (final Throwable ignored) {}
        return null;
    }

    private static void findFiles(final File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                final File[] filesInDir = file.listFiles();
                if (filesInDir != null) for (final File f : filesInDir) findFiles(f);
            } else {
                final String fileName = file.getName().toLowerCase();
                if (fileName.contains(".")) {
                    final String[] fileNameData = fileName.split("\\.");
                    if (fileNameData[1].equals("log")) {
                        if (fileName.equals(latestLog)) afterOtherFiles.add(file);
                        else {
                            final Date date = getDate(fileNameData[0], "yyyy-MM-dd-HH");
                            if (date != null) file.setLastModified(date.getTime());
                        }
                        files.add(file);
                    }
                }
            }
        }
    }

    private static void printLogData(final PrintWriter printWriter, final File file, final String fileName, final int logNumber) throws Throwable {
        if (logNumber != 1) for (int i = 0; i < 2; i++) printWriter.println("");
        printWriter.println("--------< Log Number: " + (logNumber < 10 ? "0" + logNumber : logNumber) + " | File: " + fileName + " | Last Modification: " + new Date(file.lastModified()) + " | File Size: " + file.length() + " bytes >--------");
        for (int i = 0; i < 2; i++) printWriter.println("");
        final Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) printWriter.println(scanner.nextLine());
        scanner.close();
    }

    public static void main(final String... args) {
        try {
            findFiles(new File("."));
            if (!files.isEmpty()) {
                if (!afterOtherFiles.isEmpty()) {
                    afterOtherFiles.sort(Comparator.comparingLong(File::lastModified));
                }
                files.sort(Comparator.comparingLong(File::lastModified));
                final File mergedLogFile = new File(files.size() + "-merged-logs.log");
                if (mergedLogFile.exists()) mergedLogFile.delete();
                if (mergedLogFile.createNewFile()) {
                    final PrintWriter printWriter = new PrintWriter(mergedLogFile);
                    int logNumber = 0;
                    boolean logFilesFound = false;
                    for (final File file : files) {
                        final String fileName = file.getName();
                        if (afterOtherFiles.contains(file) || fileName.equals(mergedLogFile.getName())) continue;
                        logFilesFound = true;
                        logNumber++;
                        printLogData(printWriter, file, fileName, logNumber);
                    }
                    if (!afterOtherFiles.isEmpty()) {
                        logFilesFound = true;
                        for (final File file : afterOtherFiles) {
                            logNumber++;
                            printLogData(printWriter, file, file.getName(), logNumber);
                        }
                    }
                    printWriter.close();
                    if (logFilesFound)
                        System.out.println("Done! Created merged log File with " + files.size() + " log/s and a size of " + mergedLogFile.length() + " bytes.");
                    else System.err.println("No log files to merge found!");
                } else System.err.println("Failed to create log merge File!");
            } else System.err.println("No files found!");
        } catch (final Throwable throwable) {
            System.err.println("Failed to merge cause of: " + throwable);
        }
    }

}
