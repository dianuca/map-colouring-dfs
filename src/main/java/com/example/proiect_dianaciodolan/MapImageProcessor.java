package com.example.proiect_dianaciodolan;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MapImageProcessor {
    private BufferedImage imagineOriginala;
    private int width; // latime imagine
    private int height; // inaltime imagine
    private boolean[][] granita; // pt fiecare pixel: true-granita, false-regiune/zona libera
    private boolean[][] vizitat; // pt fiecare pixel verificam daca a fost vizitat
    private int[][] regiuniLabels; // ex: 1 daca pixelul apartine regiunii 1, 2 pt 2 etc
    private int pragGranita = 100; //pragul pentru determinarea granitei

    // mapa pt salvarea regiunilor detectate
    private final Map<Integer, Region> regiuni = new HashMap<>();

    // culorile folosite pentru colorarea mapei
    private final Color[] paletaCulori = {
            new Color(255, 99, 132),
            new Color(54, 162, 235),
            new Color(255, 206, 86),
            new Color(75, 192, 192)
    };

    // metoda folosita pt a returna colectia de regiuni detectate
    public Map<Integer, Region> getRegiuni() {
        return regiuni;
    }

    // metoda folosita pentru a initializa obiectul cu imaginea originala
    public void intializare(BufferedImage image) {
        this.imagineOriginala = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.granita = new boolean[width][height];
        this.vizitat = new boolean[width][height];
        this.regiuniLabels = new int[width][height];
    }

    // metoda principala pt procesarea
    public void procesareImagine() {
        if (imagineOriginala == null) {
            throw new IllegalStateException("Nu a fost incarcata nicio imagine");
        }
        regiuni.clear(); // stergem regiunile vechi
        // resetam matricile
        this.granita = new boolean[width][height];
        this.vizitat = new boolean[width][height];
        this.regiuniLabels = new int[width][height]; // ex: 1 daca pixelul apartine regiunii 1, 2 pt 2 etc
        construireGranita();
        detectareRegiuni();
        construireAdiacenta();
        colorareHarta(); //fiecare regiune o sa aiba un id de culoare
    }

    // metoda folosita pentru construirea granitelor
    private void construireGranita() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c = new Color(imagineOriginala.getRGB(x, y), true);
                // transform culoarea intr-o valoare aproximativa de gri
                int pixelGri = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                // pixelii inchisi la culoare ii consider granita
                granita[x][y] = pixelGri < pragGranita;
            }
        }
    }

    // metoda folosita pentru a detecta regiunile
    private void detectareRegiuni() {
        int idRegiuneCurent = 1; // regiunea 1 - id 1, regiunea 2 - id 2
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // daca pixelul nu este granita si nu a fost vizitat
                if (!granita[x][y] && !vizitat[x][y]) {
                    Region region = new Region(idRegiuneCurent);
                    // apelam DFS pentru a gasi toti pixelii conectati
                    dfsRegion(x, y, idRegiuneCurent, region);
                    // daca regiunea atinge marginea, o consideram fundal si o ignoram
                    if (esteInExterior(region)) {
                        removeRegionLabels(region);
                        continue;
                    }
                    // ignoram si regiunile foarte mici
                    if (region.getPixels().size() <= 50) {
                        removeRegionLabels(region);
                        continue;
                    }
                    regiuni.put(idRegiuneCurent, region);
                    idRegiuneCurent++;
                }
            }
        }
    }

    // DFS pentru detectarea pixelilor din aceeasi regiune
    private void dfsRegion(int startX, int startY, int regionId, Region region) {
        Stack<int[]> stack = new Stack<>(); //stiva
        stack.push(new int[]{startX, startY}); //adaugarea pixelului in stiva, punctul de start
        while (!stack.isEmpty()) { //cat timp mai exista pixeli in stiva
            int[] p = stack.pop(); //scot ultimul pixel adaugat in stiva
            int x = p[0]; //coordonata x din vectorul p
            int y = p[1]; //coordonata y din vectorul p
            if (!inside(x, y)) continue; //verific daca pixelul este in interiorul imaginii
            if (vizitat[x][y]) continue; //verific daca pixelul a fost vizitat
            if (granita[x][y]) continue; //verific daca pixelul face parte din granita
            vizitat[x][y] = true; //marchez pixelul curent ca vizitat
            regiuniLabels[x][y] = regionId; //salvez id-ul regiunii cu id-ul regiunii pe care o explorez
            // salvam pixelul nu ca pereche, ci ca un singur numar
            region.addPixel(y * width + x); //transform pixelul din coordonate 2D intr-un index unic 1D
            stack.push(new int[]{x + 1, y}); //adaug in stiva pixelul din dreapta
            stack.push(new int[]{x - 1, y}); //adaug pixelul din stanga
            stack.push(new int[]{x, y + 1}); //adaug pixelul de jos
            stack.push(new int[]{x, y - 1}); //adaug pixelul de sus
        }
    }

    //metoda folosita pentru a verifica daca o regiune imi atinge marginea imaginii
    private boolean esteInExterior(Region region) {
        for (Integer index : region.getPixels()) { //parcurg toti pixelii din regiune
            //transform pixelul in coordonate x y
            int y = index / width;
            int x = index % width;
            //verific daca in regiune exista vreun pixel care sa fie pe marginea imaginii (in afara granitei)
            if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                return true;
            }
        }
        return false;
    }

    //metoda folosita pentru a sterge regiunea din matricea cu regiuni
    private void removeRegionLabels(Region region) {
        //parcurg toti pixelii din regiunie
        for (Integer index : region.getPixels()) {
            //ii convertesc astfel incat sa fie preche x y
            int y = index / width;
            int x = index % width;
            regiuniLabels[x][y] = 0; //pixelul nu mai apartine acestei regiuni
        }
    }

    // metoda care construieste vecinatatile dintre regiuni (matricea de adiacenta)
    private void construireAdiacenta() {
        //parcurg toate regiunile
        for (Region region : regiuni.values()) {
            //parcurg toti pixelii din acea regiune
            for (Integer index : region.getPixels()) {
                int y = index / width;
                int x = index % width;
                // caut in jurul pixelului (ma uit la vecini, pe o zona mai existinsa)
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        if (dx == 0 && dy == 0) continue; //sar peste pixelul curent
                        //calculez pot=zitia pixelui vecin
                        int nx = x + dx;
                        int ny = y + dy;
                        if (!inside(nx, ny)) continue; //verific daca pixelul este in imagine (ci nu e in exterior)
                        int altId = regiuniLabels[nx][ny]; //vad din ce regiune face parte pixelul vecin
                        //verific daca e vecin valid (sa apartina unei regiuni, sa nu fie aceeasi regiune, regiunea sa existe)
                        if (altId > 0 && altId != region.getId() && regiuni.containsKey(altId)) {
                            region.addNeighbor(altId); //adaug vecinul
                        }
                    }
                }
            }
        }
    }

    // metoda care porneste colorarea tuturor regiunilor
    private boolean colorareHarta() {
        return dfsColorare();
    }

    // DFS recursiv pentru colorarea regiunilor a.i sa nu aiba aceeasi culoare
    private boolean dfsColorare() {
        Region regiuneCurenta = null; //variabila unde pun regiunea pe care o colorez acum
        // caut prima regiune care nu are inca culoare
        for (Region region : regiuni.values()) {
            if (region.getCuloareRegiune() == -1) { //-1 semnifica ca nu e colorata
                regiuneCurenta = region; // salvez regiunea si ma opresc pt a o colora s
                break;
            }
        }
        // daca nu mai exista regiuni necolorate, inseamna ca am terminat de colorat
        if (regiuneCurenta == null) {
            return true;
        }
        // incerc pe rand toate culorile din paleta de culori
        for (int culoare = 0; culoare < paletaCulori.length; culoare++) {
            //verific daca culoarea poate fi pusa pe regiune (niciun vecin sa nu aiba aceeasi culoare)
            if (culoareValida(regiuneCurenta, culoare)) {
                regiuneCurenta.setCuloareRegiune(culoare); //adaug culoarea regiunii
                // merg mai departe la urmatoarea regiune
                if (dfsColorare()) { //continui colorarea
                    return true; //daca merge metoda intoarce true
                }
                // daca nu a mers, sterg culoarea si incerc alta
                regiuneCurenta.setCuloareRegiune(-1);
            }
        }
        return false; //daca nicio culoare nu merge, dau false
    }

    // verifica daca o culoare poate fi pusa pe o regiune
    private boolean culoareValida(Region region, int culoare) {
        //parcurg vecinii regiunii
        for (Integer vecinId : region.getNeighbors()) {
            Region vecin = regiuni.get(vecinId); //extrag vecinul
            if (vecin != null && vecin.getCuloareRegiune() == culoare) { //verific daca vecinul are culoare pe care vreau sa o pun
                return false; //returnez fals daca vecinul are culoarea
            }
        }
        return true;
    }

    //metoda care imi creaza imaginea finala colorata
    public BufferedImage creareImagineColorata() {
        if (imagineOriginala == null) {
            throw new IllegalStateException("Nu exista imagine.");
        }
        //creez o imagine goala cu transparenta
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //copiez imaginea originala peste cea noua
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(imagineOriginala, 0, 0, null);
        g2.dispose();
        //copiez regiunile
        for (Region region : regiuni.values()) {
            int colorIndex = region.getCuloareRegiune(); //iau culoarea regiunii
            if (colorIndex < 0 || colorIndex >= paletaCulori.length) { //daca nu are culoare o ignor
                continue;
            }
            Color fill = paletaCulori[colorIndex]; //iau culoarea din paleta
            for (Integer index : region.getPixels()) { //parcurg pixelii regiunii
                //transform indexul pt a reface coordonatele pixelului
                int y = index / width;
                int x = index % width;
                result.setRGB(x, y, fill.getRGB()); //setez culoarea pixelului in imagine
            }
        }
        // redesenez granitele cu negru
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (granita[x][y]) { //daca pixelul este granita il colorez cu negru
                    result.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return result;
    }

    // creeaza o imagine necolorata, dar etichetata cu id ul regiunii si numarul de pixeli
    public BufferedImage creareImagineDebug() {
        if (imagineOriginala == null) {
            throw new IllegalStateException("Nu exista imagine.");
        }
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(imagineOriginala, 0, 0, null);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        for (Region region : regiuni.values()) {
            int minX = width;
            int minY = height;
            int maxX = -1;
            int maxY = -1;
            long sumaX = 0;
            long sumaY = 0;
            int nrPixeli = 0;
            for (Integer index : region.getPixels()) {
                int y = index / width;
                int x = index % width;
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                sumaX += x;
                sumaY += y;
                nrPixeli++;
                // pun un efect discret peste regiune, ca sa se vada mai bine zona
                if (((x + y) % 9) == 0) {
                    result.setRGB(x, y, new Color(255, 255, 255, 90).getRGB());
                }
            }
            if (nrPixeli == 0) {
                continue;
            }
            int centruX = (int) (sumaX / nrPixeli);
            int centruY = (int) (sumaY / nrPixeli);
            // daca centrul cade pe granita, mut eticheta spre centrul bounding-box-ului
            if (!apartineRegiunii(centruX, centruY, region.getId())) {
                centruX = (minX + maxX) / 2;
                centruY = (minY + maxY) / 2;
            }
            int latimeBox = Math.max(72, (maxX - minX) / 3);
            int inaltimeBox = 36;

            int boxX = clamp(centruX - latimeBox / 2, 2, Math.max(2, width - latimeBox - 2));
            int boxY = clamp(centruY - inaltimeBox / 2, 2, Math.max(2, height - inaltimeBox - 2));
            g2.setColor(new Color(255, 255, 255, 215));
            g2.fillRoundRect(boxX, boxY, latimeBox, inaltimeBox, 12, 12);
            g2.setColor(new Color(30, 41, 59, 220));
            g2.drawRoundRect(boxX, boxY, latimeBox, inaltimeBox, 12, 12);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String linia1 = "ID " + region.getId();
            String linia2 = nrPixeli + " px";
            int text1X = boxX + (latimeBox - fm.stringWidth(linia1)) / 2;
            int text2X = boxX + (latimeBox - fm.stringWidth(linia2)) / 2;
            g2.drawString(linia1, text1X, boxY + 15);
            g2.drawString(linia2, text2X, boxY + 29);
        }

        g2.dispose();
        return result;
    }

    private boolean apartineRegiunii(int x, int y, int regionId) {
        return inside(x, y) && regiuniLabels[x][y] == regionId;
    }

    private int clamp(int valoare, int minim, int maxim) {
        return Math.max(minim, Math.min(valoare, maxim));
    }

    //metoda folosita pentru informatiile despre regiuni
    public String buildRegionsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Regiuni detectate: ").append(regiuni.size()).append("\n\n");

        for (Region region : regiuni.values()) {
            sb.append("Regiunea ").append(region.getId())
                    .append(" | pixeli=").append(region.getPixels().size())
                    .append(" | vecini=").append(region.getNeighbors())
                    .append(" | culoare=").append(region.getCuloareRegiune())
                    .append("\n");
        }

        return sb.toString();
    }

    private boolean inside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}