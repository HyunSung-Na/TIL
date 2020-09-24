import {Form, Input, Button} from "antd";
import {useCallback, useState} from "react";
import Link from "next/link";
import AppLayout from "./AppLayout";
import styled from 'styled-components';
import PropTypes from 'prop-types';
import useInput from "../hooks/useInput";

const ButtonWrapper = styled.div`
    margin-top: 10px;
`;

const FormWrapper = styled(Form)`
    padding: 10px;
`

const LoginForm = ({setIsLoggedIn}) => {
    const [id, onChangeId] = useInput('');
    const [password, onChangePassword] = useInput('');

    const onSubmitForm = useCallback(() => {
        console.log(id, password);
        setIsLoggedIn(true);
    }, [id, password]);

    return (
        <FormWrapper onFinish={onSubmitForm}>
            <div>
                <label htmlFor="user-id">아이디</label>
                <br />
                <Input name="user-id" value={id} onChange={onChangeId} required />
            </div>
            <div>
                <label htmlFor="password">비밀번호</label>
                <br />
                <Input name="password" type="password" value={password} onChange={onChangePassword} required />
            </div>
            <ButtonWrapper>
                <Button type="primary" htmlType="sumit" loading={false}>로그인</Button>
                <Link href="/signup"><a><button>회원가입</button></a></Link>
            </ButtonWrapper>
        </FormWrapper>
    )
}

LoginForm.propTypes = {
    setIsLoggedIn: PropTypes.func.isRequired,
}

export default LoginForm;