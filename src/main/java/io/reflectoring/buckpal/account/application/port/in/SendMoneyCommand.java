package io.reflectoring.buckpal.account.application.port.in;

import io.reflectoring.buckpal.account.domain.Account.AccountId;
import io.reflectoring.buckpal.account.domain.Money;
import io.reflectoring.buckpal.common.SelfValidating;
import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.validation.constraints.NotNull;

/**
 * 잘못된 입력을 호출자에게 돌려주는 유스케이스 보호막 역할의 커맨드 객체
 * 사실 상 오류 방지 계층(anti corruption layer)
 * 해당 보호막을 통해 신성한 유스케이스 코드를 외부 값 유효성 검증 코드로 오염시키지 않는다
 */
@Value
@EqualsAndHashCode(callSuper = false)
public
class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

    @NotNull
    private final AccountId sourceAccountId;

    @NotNull
    private final AccountId targetAccountId;

    @NotNull
    private final Money money;

    public SendMoneyCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        this.validateSelf(); // Java Bean Validation API
    }

}
