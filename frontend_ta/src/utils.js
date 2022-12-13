import { useNavigate } from "react-router-dom";
import { useEffect } from 'react'
async function postData(url = '', data = {}) {
  // Default options are marked with *
  const response = await fetch(process.env.REACT_APP_BACKEND_ADDRESS + url, {
    method: 'POST', // *GET, POST, PUT, DELETE, etc.
    mode: 'cors', // no-cors, *cors, same-origin
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    credentials: 'include', // include, *same-origin, omit
    headers: {
      'Content-Type': 'application/json'
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    redirect: 'follow', // manual, *follow, error
    referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  });
  
  if (response.status == 200) {
    let textresponse = await response.text();
    if (textresponse.length == 0) {
      let result = { result: null, status: response.status }
      return result;
    }
    else {
      let result = { result: JSON.parse(textresponse), status: response.status }
      return result;
    }
  }
  else {
    let errortext = await response.text();
    let result = { status: response.status, message: errortext };
    return result;
  }
}
export default postData;

export async function postForm(url = '', formData) {
  const response = await fetch(process.env.REACT_APP_BACKEND_ADDRESS + url, {
    method: 'POST', // *GET, POST, PUT, DELETE, etc.
    mode: 'cors', // no-cors, *cors, same-origin
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    credentials: 'include', // include, *same-origin, omit
    redirect: 'follow', // manual, *follow, error
    referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: formData
  });
  let result;
  if (response.status == 200) {
    result = await response.json();

    result.status = response.status;
    return result;
  }
  else {
    let errortext = await response.text();
    result = { status: response.status, message: errortext };
    return result;
  }
}

  export async function getData(url =''){
    const response = await fetch(process.env.REACT_APP_BACKEND_ADDRESS + url, {
      credentials: 'include', // include, *same-origin, omit
    });
  if (response.status === 200){
       let textresponse= await response.text();
       if(textresponse.length===0){
       let result= {result:null,status: response.status}
       return result;
    }
    else{
    let result ={result: JSON.parse(textresponse), status : response.status }
    return result;
    }
  }
  else {
    let errortext = await response.text();
    let result = { status: response.status, message: errortext };
    return result;
  }
}

export function GoToErrorPage(props) {
  let navigate = useNavigate();
  useEffect(() => {
    navigate(props.path, { state: { message: props.message } });
  }
  );
}
export function  GoToPlayPage(props) {
  let navigate = useNavigate();
  useEffect(() => {
    navigate("/play", { state: { info: props.info} });
  }
  );
}

export function checkPassword(password) {
  return /\d/.test(password) && /[A-Z]/.test(password) && /[a-z]/.test(password) && password.length >= 8 && password.length <= 20 && !/[^A-Za-z0-9]/.test()
}
export function checkUsername(username) {
  return /^[a-zA-Z][a-zA-Z0-9]{2,19}$/.test(username);
  // return username.length>=8 && username.length<=20  && !/[^A-Za-z0-9]/.test(username)  && !username.match(/[0-9](.)*/)
}
export function checkEmail(mail)
{
 if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail))
  {
    return (true)
  }

    return (false)
}
