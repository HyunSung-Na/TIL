package set;

public class MemberHashsetTest {
    public static void main(String[] args) {
        MamberHashset manager = new MamberHashset();

        Member memberLee = new Member(100,"Lee");
        Member memberKim = new Member(200,"Kim");
        Member memberPack = new Member(300,"Pack");
        Member memberPack2 = new Member(300,"Pack");

        manager.addMember(memberLee);
        manager.addMember(memberKim);
        manager.addMember(memberPack);
        manager.addMember(memberPack2);

        manager.showAllMember();

        manager.removeMember(100);
        manager.showAllMember();
    }
}
