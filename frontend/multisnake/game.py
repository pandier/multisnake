import pygame
import random
from player import Player


def loop(screen, clock):
    time = 0
    running = True

    players: list[Player] = [Player(pygame.Vector2(0, 0), pygame.Vector3(0, 128, 255))]

    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

        screen.fill("black")

        if time > 0.3:
            for player in players:
                player.tick(random.randint(0, 1) == 1)

            time = 0

        for player in players:
            player.update()
            player.render(screen)

        pygame.display.flip()

        dt = clock.tick() / 1000
        time += dt
