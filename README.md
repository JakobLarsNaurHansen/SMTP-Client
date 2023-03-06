# SMTP-Client

## Resources

### Dialog
This is what a telnet dialog might look like. `s` is server and `c` is the client. We will implement the client. That means `s` will be the messages we recieve and `c` will be our responses.

``` text
s:220 3bfd476f1de2 smtp4dev ready
c:helo name
s:250 Nice to meet you
c:mail from: test@test.com
s:250 New message started
c:rcpt to: <hank@mail.com>
s:250 Recipient accepted
c:data
s:354 End message with period
c:this is my message
.
s:250 Mail accepted
c:quit
s:221 Goodbye

```


### Basic status code

Here is a a list of SMTP status codes. This was all found on wikipedia.

<https://en.wikipedia.org/wiki/List_of_SMTP_server_return_codes>


A "Basic Status Code" SMTP reply consists of a three digit number (transmitted as three numeric characters) followed by some text. The number is for use by automata (e.g., email clients) to determine what state to enter next; the text ("Text Part") is for the human user.

The first digit denotes whether the response is good, bad, or incomplete:

- 2yz (Positive Completion Reply): The requested action has been successfully completed.
- 3yz (Positive Intermediate Reply): The command has been accepted, but the requested action is being held in abeyance, pending receipt of further information.
- 4yz (Transient Negative Completion Reply): The command was not accepted, and the requested action did not occur. However, the error condition is temporary, and the action may be requested again.
- 5yz (Permanent Negative Completion Reply): The command was not accepted and the requested action did not occur. The SMTP client SHOULD NOT repeat the exact request (in the same sequence).

The second digit encodes responses in specific categories:

- x0z (Syntax): These replies refer to syntax errors, syntactically correct commands that do not fit any functional category, and unimplemented or superfluous commands.
- x1z (Information): These are replies to requests for information.
- x2z (Connections): These are replies referring to the transmission channel.
- x3z : Unspecified.
- x4z : Unspecified.
- x5z (Mail system): These replies indicate the status of the receiver mail system.

2yz Positive completion:

- 211 System status, or system help reply
- 214 Help message (A response to the HELP command)
- 220 <domain> Service ready
- 221 <domain> Service closing transmission channel
- 221 2.0.0 Goodbye [1]
- 235 2.7.0 Authentication succeeded [3]
- 240 QUIT
- 250 Requested mail action okay, completed
- 251 User not local; will forward
- 252 Cannot verify the user, but it will try to deliver the message anyway

3yz Positive intermediate:

- 334 (Server challenge - the text part contains the Base64-encoded challenge) [3]
- 354 Start mail input

## Assignment questions

### 1

Use the following command to connect to a mail server on port 2526. A more standard port is port 25.

``` bash
# Both works
telnet datacomm.bhsi.xyz 2526
# OR
telnet 130.225.170.65 2526
```

This is the messages we need to send to the server.
```sh
helo name

mail from: test@test.com

rcpt to: <hank@mail.com>

data

this is my message
.
```



### 2
    
