# 볼링 게임 점수판
## 진행 방법
* 볼링 게임 점수판 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 2단계

### 도메인 분석

- 플레이어는 10 프레임 동안 플레이한다.
- 각 프레임마다 최대 두 번 투구한다.
- 프레임의 첫 투구에서 스트라이크를 했다면 다음에 던지지 않는다.
- 첫 투구에서 남은 핀이 있으면 한 번 더 던진다.
    - 남은 핀을 다 제거하면 스페어 상태가 된다.
    - 남은 핀이 있다면 미스 상태가 된다.
- 투구에서 핀을 하나도 쓰러트리지 못하면 거터 이다.
- 10 프레임에서는 초구 스트라이크 또는 2구 에서 스페어를 한 경우에만 세 번째 투구를 진행한다.
    - 10 프레임에서 최대 세 번의 스트라이크가 가능하다.
    
### 설계

- 볼링
    - 프레임
        - 한 개에서 세 개 까지의 투구를 가진다.
        - 프레임이 끝나면 각 투구의 결과를 종합한 상태를 보유한다.
    - 투구(pitch)
        - 각 투구는 쓰러뜨린 핀의 개수를 가지고 있다.

### 결과

- Player
    - PlayerName: 이름 제약조건 책임
    - Bowling: 볼링 게임 컨트롤
        - Frames: 프레임 목록을 제어
            - Frame: 한 프레임의 상태를 제어
                - Pins: 현재 프레임에 남아있는 핀을 추상화
                - Records: 각 투구 당 기록을 보관
                    - Record: 투구 1회의 기록을 추상화
        - FrameCreator: 프레임 생성 책임 부여
    
### 1차 피드백

- 피드백에서 가장 중요한 것이 규칙 2(else 예약어를 쓰지 않는다) 제거하기
    - State 패턴 적용이 필요
- 핀 개수에 대한 캐싱 적용해보기
- 테스트 공통 데이터에 대해 `@BeforeEach` 로 정리
- 그 외 가독성을 위한 코드 정리

#### `Records` 와 `Record` 의 리팩토링

분기문을 최소화하기 위해 State 패턴을 적용해봐야 한다.

- State 패턴 구조
    - State를 사용하는 `Context` 와 `State`, 그리고 `State` 를 구현한 `ConcreteState` 로 나뉜다.
- FSM 정의해보기
    - `Context` 가 Frame 일 것이므로, Frame 을 기준으로 생각
    - Frame 시작 전 (READY) -> Frame 플레이 중 (PLAYING) -> Frame 종료 (FINISHED)
        - 좀 더 세분화하면?
             - Finished 상태는 `STRIKE`, `SPARE`, `MISS` 로 구체화 가능
- `Context` 의 행동 정의
    - 공을 던지는 행위
        - 던질 때 마다 상태가 바뀌어야 함
        - 쓰려트린 핀의 개수를 출력해야 하므로, 이를 저장할 방법도 마련해야 함 
- `State` 의 행동 정의
    - `Frame` 이 종료되었는지 확인할 때 사용할 메소드

##### References

- [디자인 패턴 - 상태 패턴(State Pattern)](https://velog.io/@y_dragonrise/%EB%94%94%EC%9E%90%EC%9D%B8-%ED%8C%A8%ED%84%B4-%EC%83%81%ED%83%9C-%ED%8C%A8%ED%84%B4State-Pattern)
- [State](https://refactoring.guru/design-patterns/state)
    
### 2차 피드백

- Domain 과 View 분리
    - 모든 미션에서 항상 피드백 받은 부분
    - 이제 그만 실수할 때도 됐는데..
    - `State` 및 `RollResult` 에서 뷰 로직이 들어있는 상황
    - DTO 패키지에서 출력될 방식을 결정
- `FinalFrame` 개선
    - `LinkedList` 로 `State` 목록 관리
    - 추가 투구 횟수를 별도의 클래스로 관리

#### 리팩토링 방안

- Domain 과 View 분리
    - `Enum` 을 활용하여 Converter 를 다시 만들고, 각 구현체에 Enum 멤버를 직접 삽입하여 구현
    - `RollResult` 의 Child Class 모두 제거
- `FinalFrame` 개선
    - `LinkedList` 로 `State` 목록 관리
    - 추가 투구 횟수를 별도의 클래스로 관리

