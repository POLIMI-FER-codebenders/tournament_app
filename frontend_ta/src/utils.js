import { useNavigate } from "react-router-dom";
import { useEffect } from 'react'
async function postData(url = '', data = {}) {
  // Default options are marked with *
  const response = await fetch(url, {
    method: 'POST', // *GET, POST, PUT, DELETE, etc.
    mode: 'cors', // no-cors, *cors, same-origin
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    credentials: 'same-origin', // include, *same-origin, omit
    headers: {
      'Content-Type': 'application/json'
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    redirect: 'follow', // manual, *follow, error
    referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  });
  let result;
  if (response.status == 200){
     result = await response.json();
    result.status = response.status;
    return result;
  } 
  else {
    let errortext= await response.text();
    result = {status:response.status,message:errortext};
    return result;
    }
}
export default postData;
export async function postForm(url = '', formData) {
  const response = await fetch(url, {
    method: 'POST', // *GET, POST, PUT, DELETE, etc.
    mode: 'cors', // no-cors, *cors, same-origin
    cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
    credentials: 'include', // include, *same-origin, omit
    redirect: 'follow', // manual, *follow, error
    referrerPolicy: 'no-referrer', // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
    body: formData
  });
  let result;
  if (response.status == 200){
     result = await response.json();
    result.status = response.status;
    return result;
  } 
  else {
    let errortext= await response.text();
    result = {status:response.status,message:errortext};
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