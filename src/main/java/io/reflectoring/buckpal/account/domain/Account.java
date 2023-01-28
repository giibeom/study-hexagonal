package io.reflectoring.buckpal.account.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

/**
 * 일정 금액을 보관하는 계좌.
 * 실제 계좌의 현재 스냅숏을 제공합니다.
 * 계좌에 대한 모든 입금과 출금은 {@link Activity} 객체에 포착됩니다.
 * 한 계좌에 대한 모든 활동(acitivity)들을 항상 메모리에 한꺼번에 올리는 것은 현명한 방법이 아니기 때문에,
 * {@link ActivityWindow} 값 객체에서 포착한 지난 며칠 혹은 몇 주간의 범위에 해당하는 활동만 보유합니다.
 * <br>
 * 계좌의 총 잔액 : 첫번째 활동 전에 잔고(baselineBalance) + 활동 값들의 합계
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

	/**
	 * 계좌 고유 id
	 */
	@Getter private final AccountId id;

	/**
	 * 첫 번째 활동 바로 전의 잔고
	 */
	@Getter private final Money baselineBalance;

	/**
	 * 해당 계좌의 활동 창
	 * 현재 계좌의 전체 계좌 활동 리스트에서 특정 범위의 계좌 활동만 볼 수 있는 '창'
	 */
	@Getter private final ActivityWindow activityWindow;

	/**
	 * ID 없는 Account 엔티티 생성자
	 * 유효한 상태의 {@link Account} 엔티티만 생성할 수 있도록 팩터리 메서드를 제공
	 */
	public static Account withoutId(
					Money baselineBalance,
					ActivityWindow activityWindow) {
		return new Account(null, baselineBalance, activityWindow);
	}

	/**
	 * ID 있는 Account 엔티티 생성자
	 * 유효한 상태의 {@link Account} 엔티티만 생성할 수 있도록 팩터리 메서드를 제공
	 */
	public static Account withId(
					AccountId accountId,
					Money baselineBalance,
					ActivityWindow activityWindow) {
		return new Account(accountId, baselineBalance, activityWindow);
	}

	public Optional<AccountId> getId(){
		return Optional.ofNullable(this.id);
	}

	/**
	 * Calculates the total balance of the account by adding the activity values to the baseline balance.
	 */
	public Money calculateBalance() {
		return Money.add(
				this.baselineBalance,
				this.activityWindow.calculateBalance(this.id));
	}

	/**
	 * 해당 계좌에서 출금합니다.
	 * @return 성공하면 음수 값으로 새 activity를 만들고 true 반환, 실패하면 false를 반환합니다.
	 */
	public boolean withdraw(Money money, AccountId targetAccountId) {

		if (!mayWithdraw(money)) { // 출금하기 전 잔고를 초과하는 금액은 출금할 수 없도록 비즈니스 규칙 검사
			return false;
		}

		Activity withdrawal = new Activity(
				this.id,
				this.id,
				targetAccountId,
				LocalDateTime.now(),
				money);
		this.activityWindow.addActivity(withdrawal);
		return true;
	}

	// 해당 규칙을 지켜야 하는 비즈니스 로직 바로 옆에 규칙을 위치시키면, 위치를 정하는 것도 쉽고 추론도 쉽다
	// 도메인 엔티티 내에 비즈니스 규칙을 구현하자 (풍부한 도메인 모델)
	private boolean mayWithdraw(Money money) {
		return Money.add(
				this.calculateBalance(),
				money.negate())
				.isPositiveOrZero();
	}

	/**
	 * 해당 계좌에 입금합니다.
	 * @return 성공하면 양수 값으로 새 activity를 만들고 true 반환, 실패하면 false를 반환합니다.
	 */
	public boolean deposit(Money money, AccountId sourceAccountId) {
		Activity deposit = new Activity(
				this.id,
				sourceAccountId,
				this.id,
				LocalDateTime.now(),
				money);
		this.activityWindow.addActivity(deposit);
		return true;
	}

	@Value
	public static class AccountId {
		private Long value;
	}

}
