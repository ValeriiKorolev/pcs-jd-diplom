import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    File workDir;
    Map<String, List<PageEntry>> allWords = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        this.workDir = pdfsDir;

        for (File file : pdfsDir.listFiles()) { // перебираем файлы в папке pdfs
            var doc = new PdfDocument(new PdfReader(file));
            int pageCount = doc.getNumberOfPages();

            for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) { // просматриваем страницы документа
                var page = doc.getPage(pageNumber);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>(); // здесь храним слова и их количество на странице
                for (var word : words) { // считаем слова и формируем HashMap
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    List<PageEntry> listOFCurrentWord = new ArrayList<>();
                    if (allWords.containsKey(entry.getKey())) {
                        listOFCurrentWord = allWords.get(entry.getKey());
                    }
                    listOFCurrentWord.add(new PageEntry(file.getName(), pageNumber, entry.getValue()));
                    allWords.put(entry.getKey(), listOFCurrentWord);
                }
            }
        }

    }

    @Override
    public List<PageEntry> search(String word) {
        if (allWords.containsKey(word)) {
            return allWords.get(word);
        }
        return Collections.emptyList();
    }
}
