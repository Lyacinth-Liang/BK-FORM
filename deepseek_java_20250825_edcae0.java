import java.io.*;
import java.util.*;

public class DataCleaner {
    public static void main(String[] args) {
        String inputFile = "Fairnest_combined_updated(1).csv";
        String outputFile1 = "cleaned_data_with_preferences.csv";
        String outputFile2 = "cleaned_data_without_preferences.csv";
        
        List<String[]> withPreferences = new ArrayList<>();
        List<String[]> withoutPreferences = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            String[] headers = br.readLine().split(","); // 读取标题行
            
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                
                // 处理Column I (索引8): "Do you want to select your roommate yourself?"
                if (row.length > 8 && "Yes".equalsIgnoreCase(row[8].trim())) {
                    // 检查Column J (索引9): "If you want to select your roommate, please fill this blank with his/her netid"
                    if (row.length <= 9 || row[9] == null || row[9].trim().isEmpty()) {
                        // 检查Column B (索引1): "Your netid?" 是否有相同内容
                        boolean hasMatchingNetId = false;
                        try (BufferedReader br2 = new BufferedReader(new FileReader(inputFile))) {
                            br2.readLine(); // 跳过标题行
                            String line2;
                            while ((line2 = br2.readLine()) != null) {
                                String[] otherRow = line2.split(",");
                                if (otherRow.length > 1 && row.length > 1 && 
                                    otherRow[1].equals(row[1]) && !line2.equals(line)) {
                                    hasMatchingNetId = true;
                                    break;
                                }
                            }
                        }
                        if (!hasMatchingNetId) {
                            row[8] = "No"; // 将Column I改为"No"
                        }
                    }
                }
                
                // 根据Column J是否有内容分离数据
                if (row.length > 9 && row[9] != null && !row[9].trim().isEmpty()) {
                    withPreferences.add(row);
                } else {
                    withoutPreferences.add(row);
                }
            }
            
            // 写入有偏好的数据
            writeCSV(outputFile1, headers, withPreferences);
            // 写入无偏好的数据
            writeCSV(outputFile2, headers, withoutPreferences);
            
            System.out.println("数据清洗完成！");
            System.out.println("有偏好数据: " + withPreferences.size() + " 行");
            System.out.println("无偏好数据: " + withoutPreferences.size() + " 行");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void writeCSV(String filename, String[] headers, List<String[]> data) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            // 写入标题行
            pw.println(String.join(",", headers));
            
            // 写入数据行
            for (String[] row : data) {
                pw.println(String.join(",", row));
            }
        }
    }
}