import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class WordCounter {
    private Map<String, Integer> wordCount;

    public WordCounter() {
        wordCount = new HashMap<>();
    }

    public synchronized void updateWordCount(String word) {
        if (wordCount.containsKey(word)) {
            wordCount.put(word, wordCount.get(word) + 1);
        } else {
            wordCount.put(word, 1);
        }
    }

    public Map<String, Integer> getWordCount() {
        return wordCount;
    }
}

public class FileProcessor implements Runnable {
    private WordCounter wordCounter;
    private String fileName;

    public FileProcessor(WordCounter wordCounter, String fileName) {
        this.wordCounter = wordCounter;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            FileInputStream fis = new FileInputStream(new File(fileName));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    wordCounter.updateWordCount(word);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class MultithreadingCountTask {
    public static void main(String[] args) throws InterruptedException {
        WordCounter wordCounter = new WordCounter();
        FileProcessor[] fileProcessors = new FileProcessor[4];
        for (int i = 0; i < 4; i++) {
            fileProcessors[i] = new FileProcessor(wordCounter, "sample.txt");
        }
        Thread[] threads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(fileProcessors[i]);
            threads[i].start();
        }
        for (int i = 0; i < 4; i++) {
            threads[i].join();
        }
        Map<String, Integer> wordCount = wordCounter.getWordCount();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}