import java.io.*;
import java.util.*;

public class Step10 {
    static Map<Integer, WiseSaying> wiseSayingMap = new HashMap<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        String dirPath = System.getProperty("user.dir") + "/src" + "/data";
        String lastIdFilePath= dirPath + File.separator + "lastId.txt";
        String jsonFilePath = dirPath + File.separator + "wiseSaying.json";

        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean result = dir.mkdirs(); // 폴더가 없으면 생성
            if (result) {
                System.out.println("폴더가 생성되었습니다: " + dirPath);
            } else {
                System.out.println("폴더 생성에 실패했습니다: " + dirPath);
            }
        }
        // lastId 파일 검사 있으면 파일 불러와서 id 초기화, 없으면 0으로 초기화
        int lastId = 0;
        if (new File(lastIdFilePath).exists()) { // 해당 경로에 파일이 존재하는지 확인
            // 파일이 존재하면 BufferedReader로 파일 읽어와서 id 초기화
            try(BufferedReader br = new BufferedReader(new FileReader(lastIdFilePath))) {
                lastId = Integer.parseInt(br.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 처음 앱 실행 시 json파일에서 저장된 명언 불러오기
        if (new File(jsonFilePath).exists()) { // 해당 경로에 파일이 존재하는지 확인
            // json 파일에 저장된 정보 불러오기
            readJsonFile(jsonFilePath, wiseSayingMap);
        }else{
            // 파일이 없으면 새로 생성 (빈 배열 등 기본값 저장)
            try (FileWriter writer = new FileWriter(jsonFilePath)) {
                writer.write("[]"); // 빈 배열로 초기화
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("== 명언 앱 ==");

        while (true) {
            System.out.print("명령) ");
            String cmd = scanner.nextLine().trim();

            if (cmd.equals("종료")) {
                System.out.println("== 종료 ==");
                break;
            }else if (cmd.equals("등록")) {
                System.out.print("명언 : ");
                String contents = inputString(scanner);
                System.out.print("작가 : ");
                String writer = inputString(scanner);

                lastId++;

                // map 에 저장
                wiseSayingMap.put(lastId,new WiseSaying(lastId, contents, writer));

                System.out.println(lastId + "번 명언이 등록 되었습니다.");
            } else if (cmd.equals("목록")) {
                System.out.println("번호 / 명언 / 작가");
                System.out.println("---------------------");

                // map 에 저장된 명언 목록 출력
                for (WiseSaying wiseSaying : wiseSayingMap.values()) {
                    System.out.printf("%d / %s / %s\n", wiseSaying.getId(), wiseSaying.getContents(), wiseSaying.getWriter());
                }

            } else if (cmd.startsWith("삭제?id=")) {
                // 삭제할 id 추출
                int deleteId = Integer.parseInt(cmd.split("=")[1]);

                // 존재 유무 확인
                if (!wiseSayingMap.containsKey(deleteId)) {
                    System.out.println("존재하지 않는 명언입니다.");
                    continue;
                }

                // map에서 삭제
                wiseSayingMap.remove(deleteId);

            } else if (cmd.startsWith("수정?id=")) {
                int fixId = Integer.parseInt(cmd.split("=")[1]);

                // 존재 유무 확인
                if (!wiseSayingMap.containsKey(fixId)) {
                    System.out.println("존재하지 않는 명언입니다.");
                    continue;
                }

                // map에서 수정
                System.out.print("기존 명언 : ");
                System.out.println(wiseSayingMap.get(fixId).getContents());
                System.out.print("수정할 명언 : ");
                String newContent = inputString(scanner);
                System.out.print("기존 작가 : ");
                System.out.println(wiseSayingMap.get(fixId).getWriter());
                System.out.print("수정할 작가 : ");
                String newWriter = inputString(scanner);

                wiseSayingMap.put(fixId,new WiseSaying(fixId, newContent, newWriter));

            } else if (cmd.equals("빌드")){
                // map에 저장된 명언을 json 파일로 저장
                writeJsonFile(jsonFilePath, wiseSayingMap);
                // 마지막 id 저장
                try(FileWriter fileWriter = new FileWriter(lastIdFilePath)) {
                    fileWriter.write(String.valueOf(lastId));
                } catch (Exception e) {
                    System.out.println("파일 저장에 실패했습니다.");
                }
            }
        }
    }

    static void writeJsonFile(String filePath,Map<Integer, WiseSaying> wiseSayingMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n"); // [ 삽입하고 줄바꿈
        List<WiseSaying> list = new ArrayList<>(wiseSayingMap.values());
        for (int i = 0; i < list.size(); i++) {
            WiseSaying wiseSaying = list.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": ").append(wiseSaying.getId()).append(",\n");
            sb.append("    \"contents\": \"").append(wiseSaying.getContents()).append("\",\n");
            sb.append("    \"writer\": \"").append(wiseSaying.getWriter()).append("\"\n");
            if (i == list.size() - 1) {
                sb.append("  }\n"); // 마지막 객체 뒤에는 쉼표 없음
            } else {
                sb.append("  },\n"); // 그 외에는 쉼표 붙임
            }
        }

        sb.append("]");

        // 실제 파일에 저장
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(sb.toString());
            System.out.println("JSON 파일 저장 완료!");
        } catch (IOException e) {
            System.out.println("파일 저장에 실패했습니다.");
        }
    }

    static void readJsonFile(String filePath, Map<Integer, WiseSaying> wiseSayingMap) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String json = sb.toString().trim();

        // 대괄호 제거
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        // },{로 분리
        String[] objects = json.split("},\\s*\\{"); // \\s*는 공백 문자(스페이스, 탭, 줄바꿈) 0개 이상을 의미


        for (String objStr : objects) {
            // 중괄호 보정
            if (!objStr.startsWith("{")) objStr = "{" + objStr;
            if (!objStr.endsWith("}")) objStr = objStr + "}";

            // 각 필드 추출
            String idStr = getJsonValue(objStr, "id");
            String contents = getJsonValue(objStr, "contents");
            String writer = getJsonValue(objStr, "writer");

            // null 체크 및 값 정제
            if (writer == null || contents == null || idStr == null || idStr.trim().isEmpty()) continue;

            int id = Integer.parseInt(idStr);

            wiseSayingMap.put(id, new WiseSaying(id, contents, writer));
        }
    }

    // JSON 문자열에서 특정 key의 값을 추출
    static String getJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();

        // 콜론 뒤 공백 무시
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\t')) {
            start++;
        }

        // 문자열 값
        if (json.charAt(start) == '"') {
            int end = json.indexOf("\"", start + 1);
            if (end == -1) return null;
            return json.substring(start + 1, end); // 큰따옴표 제외
        } else {
            // 숫자 값
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
                end++;
            }
            return json.substring(start, end).trim();
        }
    }


    // 입력값이 특수문자 포함 여부 확인
    static String inputString(Scanner scanner) {
        String SPECIAL_CHAR_PATTERN = ".*[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ ].*";
        while (true) {
            String str = scanner.nextLine();
            if (str.matches(SPECIAL_CHAR_PATTERN)) {
                System.out.println("특수문자는 입력할 수 없습니다.");
                System.out.print("다시 입력해주세요: ");
            } else {
                return str;
            }
        }
    }

}
