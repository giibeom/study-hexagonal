package io.reflectoring.buckpal.account.adapter.in.web;

import io.reflectoring.buckpal.account.application.port.in.SendMoneyCommand;
import io.reflectoring.buckpal.account.application.port.in.SendMoneyUseCase;
import io.reflectoring.buckpal.account.domain.Account.AccountId;
import io.reflectoring.buckpal.account.domain.Money;
import io.reflectoring.buckpal.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// 저자 - 클래스마다 코드는 적을수록 좋다 (각 연산에 대해 가급적이면 별도의 패키지 안에 별도의 컨트롤러를 만드는 방식을 선호한다)
// 모델을 공유하지 않는 여러 작은 클래스들을 만드는 것을 두려워하지 말자
// (작은 클래스들은 더 파악하기 쉽고, 더 테스트하기 쉬우며, 동시 작업을 지원한다.)

/**
 * [웹 어댑터 구현]
 * HTTP 요청을 애플리케이션의 유스케이스에 대한 메서드 호출로 변환
 * 결과를 다시 HTTP로 변환
 * <br>
 * 즉 어떤 도메인 로직도 수행하지 않는다
 * <p>
 * 애플리케이션 계층 : {@link SendMoneyService}
 */
@WebAdapter
@RestController
@RequiredArgsConstructor
class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping(path = "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    void sendMoney(
            @PathVariable("sourceAccountId") Long sourceAccountId,
            @PathVariable("targetAccountId") Long targetAccountId,
            @PathVariable("amount") Long amount) {

        SendMoneyCommand command = new SendMoneyCommand(
                new AccountId(sourceAccountId),
                new AccountId(targetAccountId),
                Money.of(amount));

        sendMoneyUseCase.sendMoney(command);
    }

}
