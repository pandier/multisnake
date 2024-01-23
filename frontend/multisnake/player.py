import pygame
from pygame import Vector2, Vector3

TILE_SIZE: int = 30


class Player:
    pos: Vector2 = None
    dir: Vector2 = Vector2(1, 0)
    new_dir: Vector2 = dir

    tail: list[Vector2] = []
    color: Vector3 = None

    def __init__(self, pos: Vector2, color: Vector3):
        self.pos = pos
        self.color = color

    def tick(self, grow: bool):
        if self.new_dir.x != self.dir.x and self.new_dir.y != self.dir.y:
            self.dir = self.new_dir

        self.pos += self.dir
        self.tail.insert(0, self.dir)

        if not grow:
            self.tail.pop()

    def update(self):
        keys = pygame.key.get_pressed()

        if keys[pygame.K_w]:
            self.new_dir = Vector2(0, -1)
        elif keys[pygame.K_s]:
            self.new_dir = Vector2(0, 1)
        elif keys[pygame.K_a]:
            self.new_dir = Vector2(-1, 0)
        elif keys[pygame.K_d]:
            self.new_dir = Vector2(1, 0)

    def __render_tail(self, screen):
        p = Vector2(self.pos)
        for d in self.tail:
            p -= d

            x = p.x * TILE_SIZE + TILE_SIZE / 4
            y = p.y * TILE_SIZE + TILE_SIZE / 4
            w = TILE_SIZE / 2 * (abs(d.x) + 1)
            h = TILE_SIZE / 2 * (abs(d.y) + 1)

            if d.x < 0 or d.y < 0:
                x += TILE_SIZE / 2 * d.x
                y += TILE_SIZE / 2 * d.y
            pygame.draw.rect(screen, self.color, (x, y, w, h))

    def render(self, screen):
        self.__render_tail(screen)

        rect = (self.pos.x * TILE_SIZE, self.pos.y * TILE_SIZE, TILE_SIZE, TILE_SIZE)

        pygame.draw.rect(screen, self.color * 0.65, rect)
