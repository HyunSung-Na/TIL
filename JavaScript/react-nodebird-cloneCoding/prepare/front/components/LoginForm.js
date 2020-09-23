import {Form, Input, Button} from "antd";
import {useCallback, useState} from "react";
import Link from "next/link";
import AppLayout from "./AppLayout";

const LoginForm = () => {
    const [id, setId] = useState('');
    const [password, setPassword] = useState('');

    const onChangeId = useCallback((e) => {
        setId(e.target.value);
    }, []);

    const onChangePassword = useCallback((e) => {
        setPassword(e.target.value);
    }, []);

    return (
        <Form>
            <div>
                <label htmlFor="user-id">아이디</label>
                <br />
                <Input name="user-id" value={id} onChange={onChangeId} required />
            </div>
            <div>
                <label htmlFor="password">비밀번호</label>
                <br />
                <Input name="password" value={password} onChange={onChangePassword} required />
            </div>
            <div>
                <Button type="primary" htmlType="sumit" loading={false}>로그인</Button>
                <Link href="/signup"><a><button>회원가입</button></a></Link>
            </div>
        </Form>
    )
}

export default LoginForm;