package org.example.cccuser.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cccuser.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * 구독자, 이벤트를 받아서 후속 조치를 수행할 담당(혹은 타 서비스)
 */
@Service
public class KafkaConsumer {
    // 문자열 직렬, 역직렬 처리용도
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "msa-sb-products-payment", groupId = "test-group")
    public void listen(String message) {
        System.out.println("프로듀서 메세지 " + message);
        // 메세지를 받고 처리할 부분 처리
        // 알람처리, 탈퇴후 데이터 삭제 처리, 재고 보충 처리, ....
    }
    @KafkaListener(topics = "msa-sb-products-payment", groupId = "test-group")
    public void listen2(String message) {
        try {
            // 역직렬화
            PaymentDto paymentDto = objectMapper.readValue(message, PaymentDto.class);
            System.out.println("프로듀서 메세지 " + paymentDto.toString());
            // 메세지를 받고 처리할 부분 처리
            // 알람처리, 탈퇴후 데이터 삭제 처리, 재고 보충 처리, ....
        }catch (Exception e){}
    }
}
