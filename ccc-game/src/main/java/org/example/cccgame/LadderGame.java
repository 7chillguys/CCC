//import java.util.*;
//
//public class LadderGame {
//    private final int height;
//    private final int width;
//    private final List<List<Boolean>> ladder;
//    private final int failPosition; // 한 명만 꽝
//    private final List<String> players; // 플레이어 이름 리스트
//
//    public LadderGame(List<String> players, int height) {
//        this.width = players.size() - 1;
//        this.height = height;
//        this.ladder = new ArrayList<>();
//        this.players = players;
//        this.failPosition = new Random().nextInt(players.size()); // 한 명 랜덤 꽝
//
//        generateLadder();
//    }
//
//    private void generateLadder() {
//        Random random = new Random();
//        for (int i = 0; i < height; i++) {
//            List<Boolean> row = new ArrayList<>();
//            for (int j = 0; j < width; j++) {
//                boolean hasLeft = (j > 0) && row.get(j - 1);
//                row.add(!hasLeft && random.nextBoolean());
//            }
//            ladder.add(row);
//        }
//    }
//
//    public String getResult(String playerName) {
//        int startPosition = players.indexOf(playerName);
//        if (startPosition == -1) {
//            return "플레이어를 찾을 수 없습니다.";
//        }
//
//        int position = startPosition;
//        for (int i = 0; i < height; i++) {
//            if (position > 0 && ladder.get(i).get(position - 1)) {
//                position--;
//            } else if (position < width && ladder.get(i).get(position)) {
//                position++;
//            }
//        }
//        return position == failPosition ? "꽝!" : "통과!";
//    }
//
//    public void printLadder() {
//        for (List<Boolean> row : ladder) {
//            StringBuilder sb = new StringBuilder("|");
//            for (Boolean step : row) {
//                sb.append(step ? "---|" : "   |");
//            }
//            System.out.println(sb);
//        }
//    }
//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.print("참가자 수: ");
//        int playerCount = scanner.nextInt();
//        scanner.nextLine(); // 개행 문자 처리
//
//        List<String> players = new ArrayList<>();
//        System.out.println("참가자 이름을 입력하세요:");
//        for (int i = 0; i < playerCount; i++) {
//            System.out.print("플레이어 " + (i + 1) + " 이름: ");
//            players.add(scanner.nextLine());
//        }
//
//        System.out.print("사다리 높이: ");
//        int height = scanner.nextInt();
//
//        // **한 번만 LadderGame 생성 (중복 방지)**
//        LadderGame game = new LadderGame(players, height);
//        game.printLadder();
//
//        System.out.println("\n=== 결과 발표 ===");
//        for (String player : players) {
//            System.out.println(player + " → " + game.getResult(player));
//        }
//
//        scanner.close();
//    }
//}
