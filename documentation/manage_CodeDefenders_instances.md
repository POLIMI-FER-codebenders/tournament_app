# Managing CodeDefenders Instances in the Tournament Application

CodeDefenders servers can be registered, updated and removed from the Tournament Application through speficif API requests. These requests require admin authentication. You can login in the Tournament Application as admin using "admin" as username and "admin" as password (use your custom credentials if you changed the default ones).

If you don't have address or admin token of an active CodeDefenders instance you can deploy your own server: have a look at our [updated CodeDefenders repository](https://github.com/POLIMI-FER-codebenders/CodeDefenders).

## Register a new instance
Let `<TA_server_address>` be the address of the backend server of the Tournament Application, `<CD_server_address>` the address of the new instance to register and `<CD_token>` an admin token for that intance. You can register the new instance executing from the browser console:
```
fetch('<TA_server_address>/admin/api/cd-server/register’, {
  method: 'POST',
  credentials: 'include',
  body: JSON.stringify({
    "address": <CD_server_address>,
    "adminToken": <CD_token>
  }),
  headers: {
    'Content-type': 'application/json; charset=UTF-8'
  }
})
```

## Update the token of an already registered instance
Let `<TA_server_address>` be the address of the backend server of the Tournament Application, `<CD_server_address>` the address of the instance to be updated and `<CD_token>` the new admin token for that intance. You can update the token of the already registered instance executing from the browser console:
```
fetch('<TA_server_address>/admin/api/cd-server/update’, {
  method: 'POST',
  credentials: 'include',
  body: JSON.stringify({
    "address": <CD_server_address>,
    "adminToken": <CD_token>
  }),
  headers: {
    'Content-type': 'application/json; charset=UTF-8'
  }
})
```

## Remove an already registered instance
Let `<TA_server_address>` be the address of the backend server of the Tournament Application and `<CD_server_address>` the address of the instance to be removed. You can remove the already registered instance executing from the browser console:
```
fetch('<TA_server_address>/admin/api/cd-server/delete', {
  method: 'POST',
  credentials: 'include',
  body: JSON.stringify({
    "address": <CD_server_address>
  }),
  headers: {
    'Content-type': 'application/json; charset=UTF-8'
  }
})

```
WARNING: fault tolerance mechanism for unreliable CodeDefenders instances is currently not supported. Removing a CodeDefenders instance with some active games will cause those games and their tournament to fail.