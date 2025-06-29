# 📘 Blog Service

> A personal blogging platform built for my friend to share in-depth accounting lectures.  
> Designed to deliver content securely, quickly, and scalably with OAuth login and email verification.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00758F?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## 👥 Team Structure

| Role     | Headcount | Responsibility                          |
|----------|-----------|-------------------------------------------|
| Backend  | 1         | API development, project management       |
| Frontend | 1         | UI development and integration            |

---

## 🧩 Responsibilities & Contributions

### 🔧 Backend & Infrastructure

- Designed and implemented RESTful APIs with **Spring Boot**
- Built **Google/Kakao OAuth2 login** system for seamless authentication
- Implemented **email verification system** with **asynchronous processing** to enhance UX
- Built a **CI/CD pipeline using GitHub Actions** for automatic testing & deployment
- Deployed the service on **AWS EC2** and managed **S3/CloudFront** for media and content delivery

### 🚀 Achievements

- ✅ Reduced email verification response time from **3.66s → 0.4s** with async processing  
- ✅ Resolved **N+1 query problem** using **Fetch Join** and **Lazy loading** → optimized DB performance  
- ✅ Operated a small-scale live service with **~10 daily users** before migrating to Naver Blog  
- ✅ Hands-on deployment & infra experience through EC2, S3, and CloudFront configuration

---

## 🛠️ Tech Stack

### 📦 Backend & Database

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=flat-square)
![QueryDSL](https://img.shields.io/badge/QueryDSL-11B48A?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-00758F?style=flat-square&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

### 🌐 Infra & DevOps

![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=flat-square&logo=amazon-aws&logoColor=white)
![S3](https://img.shields.io/badge/AWS%20S3-569A31?style=flat-square&logo=amazon-s3&logoColor=white)
![CloudFront](https://img.shields.io/badge/CloudFront-232F3E?style=flat-square&logo=amazon-aws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)

| Category       | Stack |
|----------------|-------|
| Backend        | Spring Boot, JPA, QueryDSL |
| Database       | MySQL, Redis |
| Infra          | AWS EC2, S3, CloudFront |
| DevOps         | Docker, GitHub Actions |


---

## 🌐 Service Overview

This blog system was designed to:

- Share accounting lecture content securely and privately
- Support OAuth login via **Google** and **Kakao**
- Provide email-based account verification for user trust
- Enable continuous delivery via CI/CD

> 🔒 Currently not live – content has been migrated to Naver Blog

---
