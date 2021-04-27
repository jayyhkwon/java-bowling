package bowling.domain.concrete.frame.state;

import bowling.domain.RollResult;
import bowling.domain.engine.frame.state.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpareTest {

    private State spareState;
    private RollResult firstRoll;
    @BeforeEach
    void setUp() {
        firstRoll = RollResult.of(7);
        RollResult secondRoll = RollResult.of(3);
        spareState = new Spare(firstRoll, secondRoll);
    }

    @Test
    @DisplayName("Strike 상태는 더 이상 다른 상태로 바뀔 수 없다.")
    void cannotTransit() {
        assertThatThrownBy(() -> spareState.transit(RollResult.of(0)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("완료 상태를 확인할 때는 항상 true 를 반환한다.")
    void returnFalseIfCallIsFinished() {
        assertThat(spareState.isFinished()).isTrue();
    }

    @Test
    @DisplayName("Spare 상태에서는 마지막 투구를 / 로 표기하며, 이를 내보낸다.")
    void export() {
        assertThat(spareState.export()).isEqualTo(firstRoll.getValue() + "|/");
    }

    @Test
    @DisplayName("첫 번째 투구가 스트라이크면 Spare 상태를 만들 수 없다.")
    void cannotCreateSpareIfFirstRollIsStrike() {
        assertThatThrownBy(() -> new Spare(RollResult.of(10), RollResult.of(0)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("두 번째 투구에서 핀이 남았다면 Spare 상태를 만들 수 없다.")
    void cannotCreateSpareIfPinsRemain() {
        assertThatThrownBy(() -> new Spare(firstRoll, RollResult.of(2)))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
}
