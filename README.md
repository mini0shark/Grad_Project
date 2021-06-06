# Grad_Project-Server
졸업작품(Food Story)

* [소개](#소개)


* [제작배경](#제작배경)


* [Back-End개발(Django)](#Back-End개발(Django))


* [Front-End개발(안드로이드)](#Front-End개발(안드로이드))


### 소개
 + 레시피와 요리를 공유하는 컨셉의 소셜 네트워크 서비스 입니다. 이 어플을 통해 자신의 요리 방법을 공유하거나 자신이 먹었던 요리, 혹은 다른 사람이 공유한 레시피를 사용하여 만들었던 요리를 Story 형식으로 게시하여 다른 사용자들과 공유하는 서비스.


### 제작배경
 + 널리 알려진 인스타그램, 페이스북 등의 SNS는 주제를 하나로 국한되지 않는다. 따라서 하나의 음식이라는 하나의 주제를 정하여 사람들과 레시피, 요리를 공유하는 소셜 네트워크 서비스를 제작해 보려 한다.

 ### 구성
  ![구성도](https://user-images.githubusercontent.com/13701383/120916983-b61c8700-c6e7-11eb-8cca-df8b9d3cf6e5.png)

# [맨 위로](#Grad_Project-Server)

## **Back-End개발(Django)**
----


### **레시피 관계(DB)**
 
##### ![recipeSide](https://user-images.githubusercontent.com/13701383/120921214-92643b80-c6fd-11eb-811e-a63b52729395.png)
## 핵심 관계
 + 작성자(User)
 + 참조레시피(referencerelation)
 + 레시피 세부 정보(multiimageresult,extratip, recipe, ingredients)
 + 댓글(comment)
 + 해당 레시피에 대한 채팅(room)

### 유저 관계
##### ![userSide](https://user-images.githubusercontent.com/13701383/120921217-93956880-c6fd-11eb-8153-d33da965a6e2.png)
## 핵심 관계
+ 작성글(recipecontatiner)
+ 채팅 메세지(message)
+ 댓글, 댓글 좋아요(comment, commentlike)
+ 좋아요(followingrelation)
+ 사용자와 관련된 기록(history)
+ 글 좋아요, 북마크(recipelikerelation, recipewishlistrelation)

### 채팅 관계
##### ![chattingSid](https://user-images.githubusercontent.com/13701383/120921219-94c69580-c6fd-11eb-8cae-7be024bb6e4f.png)
## 핵심 관계
+ 참여자(user)
+ 채팅(message)
+ 채팅룸 정보(room)
+ 관련된 레시피 정보(recipecontainer)

### 댓글 관계
##### ![commentSide](https://user-images.githubusercontent.com/13701383/120921221-955f2c00-c6fd-11eb-9dc9-80b4354ffbe5.png)
## 핵심 관계
+ 작성자(user)
+ 달린 레시피(recipecontainer)


# [맨 위로](#Grad_Project-Server)


## **Front-End개발(안드로이드)**
---

## 구동 화면


### 홈 화면
##### ![그림6](https://user-images.githubusercontent.com/13701383/120918071-78226180-c6ed-11eb-8c9e-0cd63394adcc.png)


### 검색
##### ![그림7](https://user-images.githubusercontent.com/13701383/120918074-7a84bb80-c6ed-11eb-8811-59ac8414f000.png)


### 유저 페이지
##### ![그림8](https://user-images.githubusercontent.com/13701383/120918075-7b1d5200-c6ed-11eb-8699-791f3f6ab05a.png)


### 레시피 확인
##### ![그림5](https://user-images.githubusercontent.com/13701383/120918014-32659900-c6ed-11eb-9f7e-dd838e1be52f.png)


# [맨 위로](#Grad_Project-Server)
