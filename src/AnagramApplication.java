import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AnagramApplication {
    private static final int BYTE_SIZE = 256;
    private static final int NEW_LINE_CODE = 13;
    private static int[] letterIndexes = new int[BYTE_SIZE];
    private static byte[] letterCounts;
    private static byte[] byteBuffer;
    private static byte[] fileContentBytes;
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
        int previousNewLineIndex = 0;
        while (currentFileIndex < fileContentBytes.length) {
            byte b = fileContentBytes[currentFileIndex];
            if (b == NEW_LINE_CODE) {
                if (currentFileIndex - previousNewLineIndex == wordLength && isAnagram(currentFileIndex)) {
                    result.add(new String(byteBuffer, StandardCharsets.ISO_8859_1));
                }
                currentFileIndex += 2;
                previousNewLineIndex = currentFileIndex;
            } else {
                currentFileIndex++;
            }
        }
        return result;
    }

    private static boolean isAnagram(int currentFileIndex) {
        byte[] countsCopy = new byte[letterCounts.length];
        byteBuffer = new byte[wordLength];
        System.arraycopy(letterCounts, 0, countsCopy, 0, letterCounts.length);
        System.arraycopy(fileContentBytes, currentFileIndex - wordLength, byteBuffer, 0, wordLength);
        for (byte b : byteBuffer) {
            int unsignedByte = b;
            if (b < 0) {
                unsignedByte = b + BYTE_SIZE;
            }
            int letterIndex = letterIndexes[unsignedByte];
            if (--countsCopy[letterIndex] < 0) {
                return false;
            }
        }
        return true;
    }

    private static void initVars(String word, String path) throws IOException {
        fileContentBytes = Files.readAllBytes(new File(path).toPath());
        byte[] wordBytes = word.getBytes(StandardCharsets.ISO_8859_1);
        wordLength = wordBytes.length;
        byteBuffer = new byte[wordLength];
        setLetterCounts(wordBytes);
    }

    private static void setLetterCounts(byte[] wordBytes) {
        letterCounts = new byte[wordLength + 1];
        for (int i = 0; i < wordLength; i++) {
            int unsignedByte = wordBytes[i];
            if (unsignedByte < 0) {
                unsignedByte = (unsignedByte + BYTE_SIZE);
            }
            int wordIndex = letterIndexes[unsignedByte];
            if (wordIndex == 0) {
                int incremented = (i + 1);
                letterCounts[incremented]++;
                letterIndexes[unsignedByte] = incremented;
            } else {
                letterCounts[wordIndex]++;
            }
        }
    }
}
