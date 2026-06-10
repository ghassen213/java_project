package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import model.Activite;
import model.Membre;

import java.io.FileOutputStream;

public class cardpdf {

    public static String generer(Membre membre, Activite activite) {
        new java.io.File("cartes").mkdirs();

        String cheminFichier = "cartes/carte_" + membre.getLogin() + "_" + activite.getId() + ".pdf";

        try {
            Document document = new Document(new Rectangle(500, 300));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();

            PdfContentByte cb     = writer.getDirectContentUnder();
            PdfContentByte cbText = writer.getDirectContent();

            // ===================== FOND =====================

            cb.setColorFill(new BaseColor(245, 247, 250));
            cb.rectangle(0, 0, 500, 300);
            cb.fill();

            // ===================== BANDEAU GAUCHE =====================

            cb.setColorFill(new BaseColor(44, 62, 80));
            cb.rectangle(0, 0, 140, 300);
            cb.fill();

            // Cercle dans le bandeau
            cb.setColorFill(new BaseColor(52, 73, 94));
            cb.circle(70, 220, 45);
            cb.fill();

            cb.setColorFill(new BaseColor(44, 62, 80));
            cb.circle(70, 220, 38);
            cb.fill();

            // Accent vert bas gauche
            cb.setColorFill(new BaseColor(39, 174, 96));
            cb.rectangle(0, 0, 140, 6);
            cb.fill();

            // Accent vert bas droite
            cb.setColorFill(new BaseColor(39, 174, 96));
            cb.rectangle(140, 0, 360, 4);
            cb.fill();

            // ===================== TEXTE DANS LE CERCLE =====================

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, false);

       

            // ===================== TEXTE BANDEAU =====================

            Font fontClub = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   BaseColor.LIGHT_GRAY);
            Font fontSous = new Font(Font.FontFamily.HELVETICA,  8, Font.ITALIC, new BaseColor(149, 165, 166));

            Paragraph club = new Paragraph("CLUB SPORTIF", fontClub);
            club.setAlignment(Element.ALIGN_LEFT);
            club.setIndentationLeft(12);
            club.setSpacingBefore(8);
            club.setSpacingAfter(2);

            Paragraph carteTitre = new Paragraph("Carte membre", fontSous);
            carteTitre.setIndentationLeft(18);
            carteTitre.setAlignment(Element.ALIGN_LEFT);

            // ===================== CONTENU DROITE =====================

            Font fontTitreDroit = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,   new BaseColor(44, 62, 80));
            Font fontLabel      = new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,    new BaseColor(39, 174, 96));
            Font fontValeur     = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL,  new BaseColor(44, 62, 80));
            Font fontId         = new Font(Font.FontFamily.HELVETICA,  8, Font.NORMAL,  new BaseColor(189, 195, 199));

            // Nom du membre en grand
            Paragraph nomMembre = new Paragraph(membre.getPrenom() + " " + membre.getNom(), fontTitreDroit);
            nomMembre.setIndentationLeft(155);
            nomMembre.setSpacingBefore(35);
            nomMembre.setSpacingAfter(4);

            // Ligne verte sous le nom
            cb.setColorFill(new BaseColor(39, 174, 96));
            cb.rectangle(155, 215, 120, 2);
            cb.fill();

            // Référence en bas
            Paragraph idCarte = new Paragraph(
                "REF : " + String.format("%04d", membre.getId()) + "-" + String.format("%03d", activite.getId()),
                fontId
            );
            idCarte.setIndentationLeft(155);
            idCarte.setSpacingBefore(18);

            // ===================== AJOUT AU DOCUMENT =====================

            document.add(nomMembre);
            document.add(club);
            document.add(carteTitre);
            document.add(espaceur(8));
            ajouterInfo(document, "ACTIVITE", activite.getNom(),     fontLabel, fontValeur);
            ajouterInfo(document, "HORAIRE",  activite.getHoraire(), fontLabel, fontValeur);
            document.add(idCarte);

            document.close();

        } catch (Exception e) {
            System.err.println("Erreur generation PDF : " + e.getMessage());
        }

        return cheminFichier;
    }

    private static void ajouterInfo(Document doc, String label, String valeur,
                                     Font fontLabel, Font fontValeur) throws DocumentException {
        Paragraph p = new Paragraph();
        p.setIndentationLeft(155);
        p.setSpacingAfter(8);
        p.add(new Chunk(label + "\n", fontLabel));
        p.add(new Chunk(valeur, fontValeur));
        doc.add(p);
    }

    private static Paragraph espaceur(float hauteur) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(hauteur);
        return p;
    }
}