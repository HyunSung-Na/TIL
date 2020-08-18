
# [6기] 웹 백엔드 시스템 구현 스터디(SpringBoot)

# SpringBoot프로젝트를만들어보세요!(버전2.3.3)

○ https://start.spring.io/를이용하시거나STS또는IntelliJ를활용하세요.
○ Maven정보
■ groupId:com.github.prgrms,artifactId:social-server,packaging:jar
○ “Web,Jdbc,H2Database”Dependency가필요로합니다.
● UserEntity(다음장)에해당하는다음기능을만들어보세요.
○ HTTPGETapi/users를호출하면User정보가JSON형태로목록이반환됩니다.(H2DB에서읽어서)
○ HTTPGETapi/users/:userId를호출하면User정보가JSON형태로반환됩니다.(H2DB에서읽어서)
○ POSTapi/users/join에다음과같은JSON을전달하면User정보가H2DB에저장됩니다.그리고성공메시지가
Response로반환됩니다.
■ 요청JSON:{“principal”:“이메일”,“credentials”:“패스워드"}
● DTO에대한개념을찾아보고요청에대한DTO를만들어보세요.
■ 응답JSON:{“success”:true,“response”:“가입완료"}
● 응답에대한DTO도고려해보세요.
● HTTP요청을처리하는컨트롤러에대한테스트를작성하세요.
○ 해당컨트롤러에서사용하는서비스는Mock서비스로서실제DB를사용하지않고메모리상에서처리가되도록
구현하고컨트롤러가이서비스를테스트환경에서만사용할수있게해주세요.
