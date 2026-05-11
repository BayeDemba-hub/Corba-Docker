package com.corba.client;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import PDFService.*;

import java.io.*;
import java.nio.file.*;

/**
 * Client CORBA de démonstration.
 * Se connecte au serveur via le NameService et teste tous les services PDF.
 */
public class PDFClient {

    private static PDFProcessor pdfService;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════╗");
        System.out.println("║   Client CORBA PDF 1.8            ║");
        System.out.println("╚═══════════════════════════════════╝");

        try {
            // ── 1. Connexion au serveur CORBA ──
            ORB orb = ORB.init(args, null);

            org.omg.CORBA.Object objRef =
                orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            pdfService = PDFProcessorHelper.narrow(
                ncRef.resolve_str("PDFProcessor")
            );
            System.out.println("[CLIENT] Connecté au serveur PDFProcessor");
            System.out.println();

            // ── 2. Lire un PDF de test depuis le disque ──
            byte[] pdfData = readFile("test.pdf");
            System.out.println("[CLIENT] PDF chargé : " + pdfData.length + " octets");
            System.out.println();

            // ── Test 1 : Extraction de texte ──
            System.out.println(">>> TEST 1 : extractText()");
            String text = pdfService.extractText(pdfData);
            System.out.println("Texte extrait (" + text.length() + " caractères) :");
            System.out.println(text.substring(0, Math.min(200, text.length())) + "...");
            System.out.println();

            // ── Test 2 : Création de PDF ──
            System.out.println(">>> TEST 2 : createPDF()");
            byte[] created = pdfService.createPDF(
                "Rapport CORBA",
                "Ce PDF a été créé par le serveur CORBA 1.8.\n" +
                "Moteur : Apache PDFBox 1.8\n" +
                "Transport : IIOP (Internet Inter-ORB Protocol)\n"
            );
            writeFile("output_created.pdf", created);
            System.out.println("PDF créé : output_created.pdf (" + created.length + " octets)");
            System.out.println();

            // ── Test 3 : Découpage ──
            System.out.println(">>> TEST 3 : splitPDF() pages 1-2");
            byte[] split = pdfService.splitPDF(pdfData, 1, 2);
            writeFile("output_split.pdf", split);
            System.out.println("PDF découpé : output_split.pdf");
            System.out.println();

            // ── Test 4 : Fusion ──
            System.out.println(">>> TEST 4 : mergePDF()");
            byte[] merged = pdfService.mergePDF(pdfData, created);
            writeFile("output_merged.pdf", merged);
            System.out.println("PDF fusionné : output_merged.pdf");
            System.out.println();

            // ── Test 5 : Suppression de page ──
            System.out.println(">>> TEST 5 : deletePages() — supprimer page 1");
            byte[] deleted = pdfService.deletePages(pdfData, new String[]{"1"});
            writeFile("output_deleted.pdf", deleted);
            System.out.println("PDF sans page 1 : output_deleted.pdf");
            System.out.println();

            // ── Test 6 : Ajout de mot de passe ──
            System.out.println(">>> TEST 6 : addPassword()");
            byte[] secured = pdfService.addPassword(pdfData, "user123", "admin456");
            writeFile("output_secured.pdf", secured);
            System.out.println("PDF sécurisé : output_secured.pdf (mot de passe: user123)");
            System.out.println();

            // ── Test 7 : Conversion en images ──
            System.out.println(">>> TEST 7 : convertToImages() PNG 150dpi");
            String[] images = pdfService.convertToImages(pdfData, "png", 150);
            System.out.println("Images générées :");
            for (String img : images) {
                System.out.println("  → " + img);
            }
            System.out.println();

            System.out.println("✓ Tous les tests réussis !");

        } catch (PDFException e) {
            System.err.println("[ERREUR PDF] " + e.message);
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    private static void writeFile(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }
}
