# doklipnews
언론사 프로젝트입니다.
링크 참고 : https://dongbaeknews.com/main

#img에서 lazing을 처리하여 재방문 시에는 로딩이 빠르지만 초기 로딩 속도가 느림.(20250319)

#현재 이미지 로딩 속도에 따른 해결책 분석
| 최적화 방법 | 장점 | 단점 | 구현 복잡도 | 효과 정도 | 적합한 상황 |
|------------|------|------|------------|-----------|------------|
| **이미지 압축 및 최적화** | • 파일 크기 대폭 감소<br>• 원본 품질 유지 가능<br>• 서버 비용 절감<br>• 모든 사용자에게 효과적 | • 과도한 압축 시 품질 저하<br>• 추가 도구/서비스 필요<br>• 기존 이미지 모두 재처리 필요 | 중간 | 높음 | • 고해상도 이미지 사용<br>• 다수의 이미지 보유<br>• 모바일 사용자 많을 때 |
| **이미지 크기 조정** | • 불필요한 데이터 제거<br>• 디스플레이 크기에 맞춰 최적화<br>• 서버/클라이언트 부하 감소 | • 다양한 화면 크기 대응 어려움<br>• 여러 버전 관리 복잡<br>• 작업량 증가 | 중간 | 높음 | • 반응형 웹사이트<br>• 다양한 디바이스 지원<br>• 이미지 중심 콘텐츠 |
| **CDN 사용** | • 지역적 접근성 향상<br>• 서버 부하 분산<br>• 캐싱 자동화<br>• 대역폭 비용 절감 | • 추가 비용 발생<br>• 설정/관리 복잡성<br>• 캐시 무효화 관리 필요<br>• 외부 의존성 증가 | 높음 | 매우 높음 | • 글로벌 사용자 베이스<br>• 트래픽 많은 사이트<br>• 대용량 이미지 다수 |
| **브라우저 캐싱 활용** | • 재방문 시 로딩 시간 대폭 감소<br>• 서버 요청 수 감소<br>• 추가 비용 없음<br>• 구현 상대적 용이 | • 첫 방문자에게 효과 없음<br>• 캐시 갱신 관리 복잡<br>• 브라우저 정책에 의존 | 낮음 | 중간 | • 반복 방문자 많은 사이트<br>• 자주 변경되지 않는 이미지<br>• 리소스 제한적 환경 |
| **새로운 이미지 포맷 사용** | • 최신 압축 알고리즘 활용<br>• 동일 품질에서 크기 감소<br>• 투명도/애니메이션 지원 향상 | • 브라우저 호환성 제한<br>• 대체 이미지 제공 필요<br>• 변환 작업 추가 | 중간-높음 | 높음 | • 최신 기술 수용 환경<br>• 성능 중심 프로젝트<br>• 이미지 중심 사이트 |
| **이미지 스프라이트 사용** | • HTTP 요청 수 감소<br>• 작은 아이콘 관리 용이<br>• 병렬 다운로드 최적화 | • 개별 이미지 업데이트 어려움<br>• CSS 관리 복잡<br>• 대형 이미지에 부적합 | 높음 | 중간 | • 아이콘이 많은 인터페이스<br>• 작은 UI 요소 다수<br>• HTTP/1.1 환경 |

#=> 이미지 압축 
