# NexusPrism Addon Example

🇺🇸 [English](#english) | 🇧🇷 [Português](#português)

---

## English

A ready-to-use template for creating addons for the [NexusPrism](https://github.com/TigerDevLabs/NexusPrism) Minecraft plugin.

### Features

This template demonstrates:
- Registering custom **items** via YAML
- Registering custom **machines** via YAML
- Registering **crafting recipes** via YAML
- Registering **Infinity Crafting Table** recipes via YAML
- Registering **machine processing recipes** programmatically (Java)
- Reading data from all NexusPrism modules via **providers** (MMO, Chat, Protections, Events, Holograms, Backpacks, Traits, Discord, Jobs)

### Requirements

- Java 21+
- Maven 3.8+
- A server running NexusPrism `0.1.0-SNAPSHOT` or later

### Getting Started

1. **Fork or download** this repository
2. Edit `pom.xml` — change `groupId`, `artifactId`, and `version` to your own
3. Edit `src/main/resources/nexusprism-addon.yml` — set your addon `id`, `name`, `author`, and `main` class
4. Implement your logic in `ExampleAddon.java` (or rename/replace the class)
5. Add your content to the YAML files under `src/main/resources/`
6. Build:
   ```bash
   mvn package
   ```
7. Drop the generated jar from `target/` into your server's `addons/` folder

### Project Structure

```
nexusprism-addon-example/
├── pom.xml
└── src/main/
    ├── java/com/example/myaddon/
    │   └── ExampleAddon.java          # Main addon class
    └── resources/
        ├── nexusprism-addon.yml       # Addon descriptor
        ├── items.yml                  # Custom items
        ├── machines.yml               # Custom machines + YAML recipes
        ├── recipes.yml                # Crafting table recipes
        └── infinity_recipes/
            └── example_infinity.yml   # Infinity Crafting Table recipe
```

### Registering a Machine Processing Recipe (Java)

Use `MachineProcessingRegistry` to add input→output rules to any machine type from Java code, without touching YAML:

```java
MachineProcessingRegistry.register(
    MachineProcessingRecipe.builder("my_recipe_id", "ELECTRIC_FURNACE")
        .input("COPPER_ORE", 1)
        .output("COPPER_INGOT", 2)
        .time(100)           // ticks (0 = use machine default)
        .source(getId())     // used for cleanup on disable
        .build()
);
```

Always clean up in `onDisable`:

```java
@Override
public void onDisable() {
    MachineProcessingRegistry.unregisterBySource(getId());
}
```

### Using Providers

Every NexusPrism module exposes an optional provider. Always check availability first:

```java
// MMO
MmoRegistry.get().ifPresent(mmo -> {
    int level = mmo.getLevel(player.getUniqueId());
});

// Jobs
JobRegistry.get().ifPresent(jobs -> {
    Optional<String> job = jobs.getActiveJob(player.getUniqueId());
});

// Discord
DiscordRegistry.get().ifPresent(discord -> {
    discord.sendMessage("server-log", "Addon event triggered!");
});
```

### License

This template is released under the [MIT License](LICENSE). Do whatever you want with it.

---

## Português

Um template pronto para criar addons para o plugin Minecraft [NexusPrism](https://github.com/TigerDevLabs/NexusPrism).

### Funcionalidades

Este template demonstra:
- Registro de **itens** customizados via YAML
- Registro de **máquinas** customizadas via YAML
- Registro de **receitas de crafting** via YAML
- Registro de receitas para a **Mesa de Crafting Infinita** via YAML
- Registro de **receitas de processamento de máquinas** via código Java
- Leitura de dados de todos os módulos do NexusPrism via **providers** (MMO, Chat, Proteções, Eventos, Hologramas, Mochilas, Traits, Discord, Empregos)

### Requisitos

- Java 21+
- Maven 3.8+
- Servidor rodando NexusPrism `0.1.0-SNAPSHOT` ou superior

### Como Começar

1. **Faça um fork ou baixe** este repositório
2. Edite o `pom.xml` — altere `groupId`, `artifactId` e `version` para os seus
3. Edite `src/main/resources/nexusprism-addon.yml` — defina o `id`, `name`, `author` e classe `main` do seu addon
4. Implemente sua lógica em `ExampleAddon.java` (ou renomeie/substitua a classe)
5. Adicione seu conteúdo nos arquivos YAML em `src/main/resources/`
6. Compile:
   ```bash
   mvn package
   ```
7. Coloque o jar gerado na pasta `addons/` do seu servidor

### Estrutura do Projeto

```
nexusprism-addon-example/
├── pom.xml
└── src/main/
    ├── java/com/example/myaddon/
    │   └── ExampleAddon.java          # Classe principal do addon
    └── resources/
        ├── nexusprism-addon.yml       # Descriptor do addon
        ├── items.yml                  # Itens customizados
        ├── machines.yml               # Máquinas + receitas YAML
        ├── recipes.yml                # Receitas de crafting
        └── infinity_recipes/
            └── example_infinity.yml   # Receita da Mesa de Crafting Infinita
```

### Registrando Receitas de Máquina via Java

Use `MachineProcessingRegistry` para adicionar regras de entrada→saída em qualquer tipo de máquina via código, sem precisar de YAML:

```java
MachineProcessingRegistry.register(
    MachineProcessingRecipe.builder("minha_receita", "ELECTRIC_FURNACE")
        .input("COPPER_ORE", 1)
        .output("COPPER_INGOT", 2)
        .time(100)           // ticks (0 = usa o padrão da máquina)
        .source(getId())     // usado para limpeza no onDisable
        .build()
);
```

Sempre limpe no `onDisable`:

```java
@Override
public void onDisable() {
    MachineProcessingRegistry.unregisterBySource(getId());
}
```

### Usando Providers

Cada módulo do NexusPrism expõe um provider opcional. Sempre verifique a disponibilidade antes de usar:

```java
// MMO
MmoRegistry.get().ifPresent(mmo -> {
    int level = mmo.getLevel(player.getUniqueId());
});

// Empregos
JobRegistry.get().ifPresent(jobs -> {
    Optional<String> job = jobs.getActiveJob(player.getUniqueId());
});

// Discord
DiscordRegistry.get().ifPresent(discord -> {
    discord.sendMessage("server-log", "Evento do addon disparado!");
});
```

### Licença

Este template é distribuído sob a [Licença MIT](LICENSE). Faça o que quiser com ele.
