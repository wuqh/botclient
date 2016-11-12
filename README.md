# botclient
微软Direct Line REST API  Java例子


由于微软的bot框架还没正式进入中国市场，所以目前的资料还比较少，由于公司需要和微软研究院合作做一个案例，所以就
根据https://docs.botframework.com/en-us/restapi/directline/#navtitle提供的说明转化成Java的代码

根据文档的说明，分三步实现与Bot的交互

1. Authentication: Secrets and Tokens
获取连接授权


```
请求
-- connect to directline.botframework.com --
POST /api/conversations/abc123/messages HTTP/1.1
Authorization: BotConnector RCurR_XV9ZA.cwA.BKA.iaJrC8xpy8qbOF5xnR2vtCX7CZj0LdjAPGfiCpg4Fv0
[other HTTP headers, omitted]
```


```
响应
{
    "conversationId": "abc123",
    "token": "RCurR_XV9ZA.cwA.BKA.iaJrC8xpy8qbOF5xnR2vtCX7CZj0LdjAPGfiCpg4Fv0y8qbOF5xPGfiCpg4Fv0y8qqbOF5x8qbOF5xn",
    "expires_in": 1800
}
```




2. REST calls for a Direct Line conversation
请求

```
-- connect to directline.botframework.com --
POST /api/conversations/abc123/messages HTTP/1.1
Authorization: Bearer RCurR_XV9ZA.cwA.BKA.iaJrC8xpy8qbOF5xnR2vtCX7CZj0LdjAPGfiCpg4Fv0
[other headers]

{
  "text": "hello",
  "from": "user1"
}

-- response from directline.botframework.com --
HTTP/1.1 204 No Content
[other headers]
```


3. Receiving Activities from the bot


```
请求
-- connect to directline.botframework.com --
GET /api/conversations/abc123/messages?watermark=0001a-94 HTTP/1.1
Authorization: Bearer RCurR_XV9ZA.cwA.BKA.iaJrC8xpy8qbOF5xnR2vtCX7CZj0LdjAPGfiCpg4Fv0
[other headers]

-- response from directline.botframework.com --
响应
HTTP/1.1 200 OK
[other headers]

{
  "messages": [{
      "conversation": "abc123",
      "id": "abc123|0000",
      "text": "hello",
      "from": "user1"
    }, {
      "conversation": "abc123",
      "id": "abc123|0001",
      "text": "Nice to see you, user1!",
      "from": "bot1"
    }
  ],
  "watermark": "0001a-95"
}
```








