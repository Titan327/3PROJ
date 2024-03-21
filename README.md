# 3PROJ

## Description

Projet de 3PROJ de Supinfo en 2023-2024

## Prérequis
Avant de commencer, assurez-vous d'avoir Docker et Docker Compose installés sur votre machine.

- [Docker Installation](https://docs.docker.com/get-docker/)
- [Docker Compose Installation](https://docs.docker.com/compose/install/)

## Commandes
Tout d'abord il faut lancer les conteneurs dockers avec cette commande:
```bash
docker-compose up -d
```
Faite cette commande pour acceder aux logs d'Express:
```bash
docker logs API-EXPRESS -f 
```
Faite cette commande pour acceder aux logs de Quasar:
```bash
docker logs API-QUASAR -f 
```
Pour build le projet veuillez lancer les conteneurs avec cette commande:
```bash
docker-compose -f prod.yaml up -d
```