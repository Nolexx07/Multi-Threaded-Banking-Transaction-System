package reporting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Minimal PDF exporter for log files without external dependencies.
 * Generates a very simple text-based PDF containing concatenated log contents.
 */
public class LogPdfExporter {

    /**
     * Export the provided log files into a simple PDF file.
     * @param outputPdfPath path to write the PDF
     * @param logFiles list of log file paths to include
     */
    public void exportLogsToPdf(String outputPdfPath, List<String> logFiles) {
        StringBuilder content = new StringBuilder();
        content.append("Banking System Logs\n\n");
        for (String logFile : logFiles) {
            content.append("=== ").append(logFile).append(" ===\n");
            content.append(readFileSafely(logFile)).append("\n\n");
        }
        writeMinimalPdf(outputPdfPath, content.toString());
        System.out.println("PDF exported to: " + outputPdfPath);
    }

    private String readFileSafely(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append("[Unable to read ").append(path).append(" : ").append(e.getMessage()).append("]\n");
        }
        return sb.toString();
    }

    /**
     * Very small PDF writer (text only, single page).
     * This avoids external libraries and produces a printable PDF.
     */
    private void writeMinimalPdf(String outputPdfPath, String text) {
        // Basic PDF objects
        String header = "%PDF-1.4\n";
        String obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
        String obj2 = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n";
        String stream = text.replace("(", "\\(").replace(")", "\\)").replace("\r", "");
        String obj3 = "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << >> >>\nendobj\n";
        String contentStream = "BT /F1 12 Tf 50 750 Td (" + stream.replace("\n", ") Tj\nT* (") + ") Tj ET";
        String obj4 = "4 0 obj\n<< /Length " + contentStream.length() + " >>\nstream\n" + contentStream + "\nendstream\nendobj\n";
        String obj5 = "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n";
        // Update page resource to include font
        obj3 = obj3.replace("/Resources << >>", "/Resources << /Font << /F1 5 0 R >> >>");

        // xref
        StringBuilder pdf = new StringBuilder();
        pdf.append(header);
        int offset1 = header.length();
        pdf.append(obj1);
        int offset2 = pdf.length();
        pdf.append(obj2);
        int offset3 = pdf.length();
        pdf.append(obj3);
        int offset4 = pdf.length();
        pdf.append(obj4);
        int offset5 = pdf.length();
        pdf.append(obj5);
        int xrefStart = pdf.length();

        pdf.append("xref\n0 6\n");
        pdf.append(String.format("%010d 65535 f \n", 0));
        pdf.append(String.format("%010d 00000 n \n", offset1));
        pdf.append(String.format("%010d 00000 n \n", offset2));
        pdf.append(String.format("%010d 00000 n \n", offset3));
        pdf.append(String.format("%010d 00000 n \n", offset4));
        pdf.append(String.format("%010d 00000 n \n", offset5));
        pdf.append("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(xrefStart).append("\n%%EOF");

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPdfPath, StandardCharsets.UTF_8))) {
            writer.write(pdf.toString());
        } catch (IOException e) {
            System.err.println("Error writing PDF: " + e.getMessage());
        }
    }
}

