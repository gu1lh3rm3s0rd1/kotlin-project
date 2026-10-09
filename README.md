# Constância — App de acompanhamento de hábitos

## Objetivo do aplicativo

O **Constância** ajuda a acompanhar hábitos do dia a dia (ex.: beber água, ler, meditar).
O usuário cadastra seus hábitos com uma meta semanal, vê a lista de hábitos e marca
quais já fez hoje. Os dados ficam salvos no celular e continuam lá depois de fechar o app.

O projeto foi feito em duas etapas, e as duas estão no mesmo app, com **um único ícone**:

| Onde fica no app | Etapa | Tecnologia |
|---|---|---|
| Tela inicial **"Meus hábitos"** | Etapa 2 | Jetpack Compose, Navigation 3, ViewModel e Room |
| Menu **⋮ (Mais opções) > "Versão original (XML)"**, no canto superior direito da tela inicial | Etapa 1 | Views com layouts XML, ViewBinding e Intent explícita (dados mockados) |

## Como rodar o projeto

### Pré-requisitos
- **Android Studio** (versão recente, com suporte ao Android Gradle Plugin 9.2).
- **Android SDK 36.1**: o Android Studio oferece para instalar automaticamente ao sincronizar o projeto.
- **JDK 21**: o Gradle baixa sozinho na primeira vez (configurado em `gradle/gradle-daemon-jvm.properties`).
- Um emulador ou celular com **Android 8.0 (API 26) ou superior**.

### Passo a passo
1. Clone o repositório e abra a pasta do projeto no Android Studio (`File > Open`).
2. Aguarde o **Gradle Sync** terminar (na primeira vez ele baixa as dependências, o que pode demorar alguns minutos).
3. Selecione a configuração **app** e um emulador/celular.
4. Clique em **Run ▶**.

O app abre na Etapa 2. Para ver a Etapa 1, toque no menu **⋮** (canto superior direito da tela inicial) e escolha **"Versão original (XML)"**. Para voltar, use o botão/gesto "voltar" do Android.

> A Etapa 1 usa dados de demonstração (mockados): marcar um hábito como feito ali muda só a tela de detalhe e não é salvo. A persistência é da Etapa 2.

Também é possível compilar pelo terminal:

```bash
./gradlew assembleDebug        # gera o APK em app/build/outputs/apk/debug/
./gradlew testDebugUnitTest    # roda os testes unitários (no computador)
./gradlew connectedDebugAndroidTest   # roda os testes no emulador/celular conectado
```

O projeto **não usa chaves de API nem senhas**, então não é preciso nenhum arquivo `.env`.

## Funcionalidades (fluxo completo da Etapa 2)

1. **Lista de hábitos**: mostra os hábitos salvos. Na primeira vez a lista está vazia e aparece o botão "Criar primeiro hábito".
2. **Criar/editar hábito**: formulário com nome, descrição (opcional) e meta semanal, com validação dos campos.
3. **Detalhe do hábito**: mostra as informações e permite marcar ou desmarcar como feito hoje, editar e excluir.
4. **Persistência**: tudo é salvo no banco local (Room). Ao fechar e abrir o app de novo, os hábitos continuam lá.

## Arquitetura

```
app/src/main/java/com/example/rotina/
├── etapa1/                     → Etapa 1 (Views/XML)
│   ├── ListaHabitosActivity    → tela 1: RecyclerView com hábitos mockados
│   ├── DetalheHabitoActivity   → tela 2: recebe o id pela Intent
│   ├── HabitoAdapter           → adapter que infla o item_habito.xml
│   └── HabitoMock              → data class + dados mockados
│
└── etapa2/                     → Etapa 2 (Compose)
    ├── RotinaApplication       → cria o banco e o Repository uma única vez
    ├── MainActivity            → ponto de entrada do Compose
    ├── data/
    │   ├── Habito              → data class do hábito (também é a tabela do Room)
    │   ├── HabitoDao           → consultas do banco
    │   ├── RotinaDatabase      → banco Room
    │   └── HabitoRepository    → acesso aos dados usado pelos ViewModels
    └── ui/
        ├── Rotas / Navegacao   → rotas e back stack do Navigation 3
        ├── ListaHabitos*       → tela de lista + ViewModel
        ├── DetalheHabito*      → tela de detalhe + ViewModel
        ├── FormularioHabito*   → formulário + ViewModel
        ├── ValidacaoHabito     → regras de validação do formulário
        ├── Componentes         → HabitoCard e telas de carregando/vazio/erro
        └── Tema                → cores do Material 3 (claro e escuro)
```

**Como os dados circulam (fluxo unidirecional):**

```
Tela (Compose)  --evento (ex.: clicou em salvar)-->  ViewModel  -->  Repository  -->  Room
      ^                                                  |
      +------------ StateFlow com o estado da tela ------+
```

- A tela só **mostra** o estado e **avisa** o ViewModel quando o usuário faz algo.
- O ViewModel guarda o estado em um `StateFlow`. A tela lê esse estado com `collectAsStateWithLifecycle()`.
- O Room devolve `Flow`, então quando um hábito muda no banco a lista se atualiza sozinha.
- As operações no banco rodam em coroutines (`viewModelScope`), que são canceladas automaticamente quando a tela é fechada.
- O ViewModel sobrevive à rotação da tela, então os dados digitados no formulário não se perdem.

## Bibliotecas utilizadas

| Biblioteca | Para que é usada |
|---|---|
| **AndroidX AppCompat, Core KTX, Material Components** | Base das telas em Views da Etapa 1 |
| **RecyclerView** | Lista de hábitos da Etapa 1 |
| **ConstraintLayout** | Biblioteca de layouts XML (vem no modelo do projeto) |
| **Jetpack Compose (BOM, UI, Material 3)** | Criação das telas da Etapa 2 com código Kotlin |
| **Material Icons Core** | Ícones (adicionar, voltar, editar, excluir) |
| **Activity Compose** | Permite usar Compose dentro de uma Activity (`setContent`) |
| **Lifecycle ViewModel Compose** | Cria os ViewModels dentro das telas Compose |
| **Lifecycle Runtime Compose** | Fornece o `collectAsStateWithLifecycle()` |
| **Navigation 3 (runtime e UI)** | Navegação entre as telas com rotas e back stack |
| **Lifecycle ViewModel Navigation3** | Faz cada tela do Navigation 3 ter o seu próprio ViewModel |
| **Kotlinx Serialization** | Permite salvar as rotas do back stack (`@Serializable`) |
| **Room (runtime, ktx, compiler)** | Banco de dados local (SQLite) para salvar os hábitos |
| **KSP** | Ferramenta que gera o código do Room durante a compilação |
| **JUnit 4** | Testes unitários (validação, Repository e ViewModels) |
| **Kotlinx Coroutines Test** | Permite testar código com coroutines (ViewModels) nos testes unitários |
| **Compose UI Test / Espresso / AndroidX Test** | Testes que abrem o app no emulador e clicam nas telas (Compose e XML) |

## Testes

**Testes unitários** (`app/src/test/.../etapa2/`, rodam no computador, sem emulador):

| Arquivo | O que verifica |
|---|---|
| `ValidacaoHabitoTest` | Regras do formulário: nome obrigatório (3 a 40 caracteres), descrição até 120, meta de 1 a 7 |
| `HabitoRepositoryTest` | Criar, editar sem duplicar, marcar/desmarcar "feito hoje" e excluir |
| `ListaHabitosViewModelTest` | Estados de lista vazia, conteúdo e **erro** (com "Tentar novamente") e o checkbox |
| `FormularioHabitoViewModelTest` | Erros de validação, dados salvos sem espaços extras, descrição vazia salva como nula, edição |
| `DetalheHabitoViewModelTest` | Hábito não encontrado, marcar como feito e excluir antes de voltar |

Esses testes usam um `FakeHabitoDao` (banco "de mentira" em memória) no lugar do Room.

**Testes instrumentados** (`app/src/androidTest/.../etapa2/`, rodam no emulador/celular):

| Arquivo | O que verifica |
|---|---|
| `HabitoDaoTest` | Consultas do Room de verdade (banco temporário em memória) |
| `FluxoHabitosTest` | Abre o app e percorre o fluxo: lista vazia → validação → criar → checkbox → detalhe → editar → excluir, além da versão XML pelo menu |

Atenção: o `FluxoHabitosTest` apaga os hábitos salvos no aparelho onde roda.

Para rodar: `./gradlew testDebugUnitTest` e `./gradlew connectedDebugAndroidTest` (com um emulador ligado),
ou clique com o botão direito na pasta `test`/`androidTest` > **Run Tests**.
