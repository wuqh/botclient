
由于微软的bot框架还没正式进入中国市场，所以目前的资料还比较少，由于公司需要和微软研究院合作做一个案例，所以就
根据https://docs.botframework.com/en-us/restapi/directline/#navtitle

提供的说明转化成Java的代码

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
HttpPost httpPost = new HttpPost(DIRECTLINE_URL);
httpPost.addHeader("Authorization", "BotConnector ****");//这里和C#的主要差别

```
响应
{
    "conversationId": "abc123",
    "token": "RCurR_XV9ZA.cwA.BKA.iaJrC8xpy8qbOF5xnR2vtCX7CZj0LdjAPGfiCpg4Fv0y8qbOF5xPGfiCpg4Fv0y8qqbOF5x8qbOF5xn",
    "expires_in": 1800
}
```
返回的conversationId用于与bot进行上下文关联。



2. REST calls for a Direct Line conversation

发送对话信息，报文必须是json格式，C#版的有Messages的对象可以直接使用。这次请求发完后响应204就结束了，结果需要用第三步取到。
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

最终的到解析结果需要通过这个请求来获取。

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
HttpPost httpPost = new HttpPost(DIRECTLINE_URL);
httpPost.addHeader("Authorization", "BotConnector ****");//这里和C#的主要差别

```
响应
{
    "conversationId": "abc123",
    "token": "RCurR_XV9ZA.cwA.BKA.iaJrC8xpy8qbOF5xnR2vtCX7CZj0LdjAPGfiCpg4Fv0y8qbOF5xPGfiCpg4Fv0y8qqbOF5x8qbOF5xn",
    "expires_in": 1800
}
```
返回的conversationId用于与bot进行上下文关联。



2. REST calls for a Direct Line conversation

发送对话信息，报文必须是json格式，C#版的有Messages的对象可以直接使用。这次请求发完后响应204就结束了，结果需要用第三步取到。
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

最终的到解析结果需要通过这个请求来获取。

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


该测试用例用的是maven工程
