import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AnagramApplication {
    private static final int BYTE_SIZE = 256;
    private static int[] wordIndexes = new int[BYTE_SIZE];
    private static byte[] wordCounts;
    private static byte[] byteBuffer;
    private static byte[] fileContent;
    private static int wordLength;

    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        initVars(args[1], args[0]);
        List<String> stringResult = getAnagrams();
        String join = String.join(", ", stringResult);
        System.out.println((System.nanoTime() - startTime) / 1000 + ", " + join);
    }

    private static List<String> getAnagrams() {
        List<String> result = new ArrayList<>();
        int currentFileIndex = 0;
        int newWordIndex = 0;
        while (currentFileIndex < fileContent.length) {
            byte b = fileContent[currentFileIndex];
            if (b == 13) {
                if (newWordIndex == wordLength && isAnagram(currentFileIndex)) {
                    result.add(new String(byteBuffer, StandardCharsets.ISO_8859_1));
                }
                byteBuffer = new byte[wordLength];
                newWordIndex = 0;
                currentFileIndex += 2;
            } else {
                currentFileIndex++;
                newWordIndex++;
            }
        }
        return result;
    }

    private static boolean isAnagram(int currentIndex) {
        byte[] countsCopy = new byte[wordCounts.length];
        System.arraycopy(wordCounts, 0, countsCopy, 0, wordCounts.length);
        System.arraycopy(fileContent, currentIndex - wordLength, byteBuffer, 0, wordLength);
        for (byte b : byteBuffer) {
            int intByte = b;
            if (b < 0) {
                intByte = b + BYTE_SIZE;
            }
            int wordIndex = wordIndexes[intByte];
            if (--countsCopy[wordIndex] < 0) {
                return false;
            }
        }
        return true;
    }

    private static void initVars(String word, String path) throws IOException {
        fileContent = Files.readAllBytes(new File(path).toPath());
        byte[] wordBytes = word.getBytes(StandardCharsets.ISO_8859_1);
        wordLength = wordBytes.length;
        setWordCounts(wordBytes);
    }

    private static void setWordCounts(byte[] wordBytes) {
        wordCounts = new byte[wordLength + 1];
        for (int i = 0; i < wordLength; i++) {
            int intByte = wordBytes[i];
            if (intByte < 0) {
                intByte = (intByte + BYTE_SIZE);
            }
            int wordIndex = wordIndexes[intByte];
            if (wordIndex == 0) {
                int incremented = (i + 1);
                wordCounts[incremented]++;
                wordIndexes[intByte] = incremented;
            } else {
                wordCounts[wordIndex]++;
            }
        }
    }
}
