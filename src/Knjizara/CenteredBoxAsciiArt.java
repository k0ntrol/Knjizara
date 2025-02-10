package Knjizara;

public class CenteredBoxAsciiArt {
    public static void main(String[] args) {

        // Korisnički redovi
        String[] lines = {
                "__...--~~~~~-._   _.-~~~~~--...__",
                " //               `V'               \\\\",
                " //                 |                 \\\\",
                " //__...--~~~~~~-._  |  _.-~~~~~~--...__\\\\",
                " //__.....----~~~~._\\ | /_.~~~~----.....__\\\\",
                " ====================\\\\|//==================== ",
                "`---`",
                "Dobrodošli u Odinovu Riznicu!",
                "Sapientia est potentia."
        };


        // Pronalazi maksimalnu dužinu
        int maxLen = 0;
        for (String line : lines) {
            if (line.length() > maxLen) {
                maxLen = line.length();
            }
        }

        // Kreiranje gornje i donje ivice okvira
        StringBuilder border = new StringBuilder();
        border.append("*");
        for (int i = 0; i < maxLen; i++) {
            border.append("-");
        }
        border.append("*");

        // Ispisujemo gornju liniju okvira
        System.out.println(border);

        // Ispis svakog reda, centriranog
        for (String line : lines) {
            int spacesBefore = (maxLen - line.length()) / 2;
            int spacesAfter  = maxLen - line.length() - spacesBefore;

            System.out.print("*");  // početak okvira

            // Praznine pre teksta
            for (int i = 0; i < spacesBefore; i++) {
                System.out.print(" ");
            }

            // Tekst
            System.out.print(line);

            // Praznine posle teksta
            for (int i = 0; i < spacesAfter; i++) {
                System.out.print(" ");
            }

            System.out.println("*"); // kraj okvira
        }

        // Ispisujemo donju liniju okvira
        System.out.println(border);
    }
}