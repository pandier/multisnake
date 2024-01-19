import pygame
import game


def init():
    pygame.init()
    # screen = pygame.display.set_mode((0, 0),pygame.FULLSCREEN)

    screen = pygame.display.set_mode((1600, 900))
    clock = pygame.time.Clock()

    game.loop(screen, clock)
    pygame.quit()


if __name__ == '__main__':
    init()
