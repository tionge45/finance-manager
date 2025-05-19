package com.finance.database.report;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class PDFReportGenerator {

    private final ReportDataCollector collector;

    public PDFReportGenerator(ReportDataCollector collector) {
        this.collector = collector;
    }

    public void generateReport(String userEmail, LocalDate startDate, LocalDate endDate,
                               ReportType type, String outputPath) {
        ReportData data = collector.collectReportData(userEmail, startDate, endDate);
        generateReportByType(data, type, outputPath);
    }

    public void generateReportByType(ReportData data, ReportType type, String outputPath) {
        switch (type) {
            case INCOME -> generateIncomeReport(data, outputPath);
            case EXPENSE -> generateExpenseReport(data, outputPath);
            case COMBINED -> generateCombinedReport(data, outputPath);
        }
    }

    public void generateIncomeReport(ReportData data, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Income Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("User: " + data.userEmail);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Period: " + data.startDate + " to " + data.endDate);
                contentStream.endText();

                int yPosition = 680;
                for (Map.Entry<String, Double> entry : data.incomeByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText(entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
                    contentStream.endText();
                    yPosition -= 15;
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition - 10);
                contentStream.showText("Total Income: $" + String.format("%.2f", data.totalIncome));
                contentStream.endText();
            }

            document.save(outputPath);
            System.out.println("Income PDF saved at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateExpenseReport(ReportData data, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Expense Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("User: " + data.userEmail);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Period: " + data.startDate + " to " + data.endDate);
                contentStream.endText();

                int yPosition = 680;
                for (Map.Entry<String, Double> entry : data.expenseByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText(entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
                    contentStream.endText();
                    yPosition -= 15;
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition - 10);
                contentStream.showText("Total Expense: $" + String.format("%.2f", data.totalExpense));
                contentStream.endText();
            }

            document.save(outputPath);
            System.out.println("Expense PDF saved at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateCombinedReport(ReportData data, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Combined Income & Expense Report");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("User: " + data.userEmail);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Period: " + data.startDate + " to " + data.endDate);
                contentStream.endText();

                int yPosition = 680;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Income:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (Map.Entry<String, Double> entry : data.incomeByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
                    contentStream.endText();
                    yPosition -= 15;
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Total Income: $" + String.format("%.2f", data.totalIncome));
                contentStream.endText();

                yPosition -= 40;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Expenses:");
                contentStream.endText();

                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (Map.Entry<String, Double> entry : data.expenseByCategory.entrySet()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText(entry.getKey() + ": $" + String.format("%.2f", entry.getValue()));
                    contentStream.endText();
                    yPosition -= 15;
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(70, yPosition);
                contentStream.showText("Total Expense: $" + String.format("%.2f", data.totalExpense));
                contentStream.endText();
            }

            document.save(outputPath);
            System.out.println("Combined PDF saved at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
