//package org.example.cccgame;
//
//import java.util.*;
//
//public class RouletteGame {
//    private final List<String> items;    // 룰렛 항목 리스트
//
//    // 생성자
//    public RouletteGame(List<String> items) {
//        this.items = items;
//    }
//
//    // 룰렛을 돌려서 결과 반환
//    public String spinRoulette() {
//        Random random = new Random();
//        int randomIndex = random.nextInt(items.size());  // 항목 중 하나를 랜덤으로 선택
//        return "룰렛 결과: " + items.get(randomIndex);
//    }
//
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        // 룰렛 항목 입력
//        System.out.print("룰렛 항목의 개수: ");
//        int itemCount = scanner.nextInt();
//        scanner.nextLine(); // 개행 문자 처리
//
//        List<String> items = new ArrayList<>();
//        System.out.println("룰렛 항목을 입력하세요:");
//        for (int i = 0; i < itemCount; i++) {
//            System.out.print("항목 " + (i + 1) + ": ");
//            items.add(scanner.nextLine());
//        }
//
//        // 룰렛 게임 생성
//        RouletteGame game = new RouletteGame(items);
//
//        // 룰렛 돌리기
//        System.out.println("\n=== 룰렛 결과 발표 ===");
//        System.out.println(game.spinRoulette());
//
//        scanner.close();
//    }
//}
