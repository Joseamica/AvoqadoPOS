---
trigger: always_on
description: 
globs: 
---

# Prisma schema using on my backend:

// This is your Prisma schema file,
// learn more about it in the docs: https://pris.ly/d/prisma-schema

generator client {
  provider = "prisma-client-js"
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

model BlumonToken {
  id        Int      @id @default(autoincrement())
  value     String
  expiresAt DateTime
}

model MentaToken {
  id        Int      @id @default(autoincrement())
  value     String
  expiresAt DateTime
}

model GlobalConfiguration {
  id        Int      @id @default(autoincrement())
  key       String
  value     String
  updatedAt DateTime @updatedAt
  createdAt DateTime @default(now())
}

model Session {
  sid    String   @id @default(uuid()) @map("sid")
  sess   Json
  expire DateTime
  User   User?    @relation(fields: [userId], references: [id])
  userId String?

  @@index([sid], name: "session_sid_idx")
  @@index([expire], name: "session_expire_idx")
  @@map("session")
}

enum StatusType {
  SUCCESS
  ERROR
  PENDING
  CANCELLED
}

enum EventType {
  ACCESS_QR
  VIEW_MENU
  ATTEMPTED_ORDER
  COMPLETED_ORDER
  CLICKED_PAYMENT_BUTTON
  ATTEMPTED_PAYMENT
  COMPLETED_PAYMENT
  ACCESS_CHECKOUT_STRIPE
  ACCESS_REGISTRATION
  COMPLETED_REGISTRATION
  ACCESS_LOGIN
  COMPLETED_LOGIN
  ACCESS_GOOGLE_LOGIN
  COMPLETED_GOOGLE_LOGIN
  GIVE_REVIEW
  ACCESS_INSTAGRAM
  OTHER
}

model Metric {
  id                 Int         @id @default(autoincrement())
  // type               String?
  count              Int?        @default(1)
  unstable_eventType EventType?
  status             StatusType?

  message          String?
  userId           String? // Puede ser null si es un usuario anónimo
  productId        String? // Si el evento está relacionado con un producto
  // Relación con el modelo de Usuario (si existe)
  venueId          String?
  venue            Venue?  @relation(fields: [venueId], references: [id])
  table            Table?  @relation(fields: [tableTableNumber, venueId], references: [tableNumber, venueId])
  tableTableNumber Int?
  billId           String?
  bill             Bill?   @relation(fields: [billId], references: [id])
  billV2           BillV2? @relation(fields: [billV2Id], references: [id])
  billV2Id         String?

  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())

  @@index([unstable_eventType])
  @@index([status])
  @@index([createdAt])
  @@index([message])
}

model Global {
  id                  String  @id @default(cuid())
  specialPaymentToken String?
}

model Chain {
  id        String    @id @default(cuid())
  name      String
  venues    Venue[]
  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())
  users     User[]
}

enum VenueType {
  RESTAURANT
  STUDIO
  BAR
  CAFE
  OTHER
}

model Venue {
  id                    String         @id @default(cuid())
  name                  String
  posName               PosNames?
  posUniqueId           String?
  chainId               String?
  chain                 Chain?         @relation(fields: [chainId], references: [id])
  address               String?
  city                  String?
  type                  VenueType?
  country               String?
  utc                   String?        @default("America/Mexico_City")
  instagram             String?
  phone                 String?
  email                 String?        @unique
  website               String?
  language              String?        @default("es")
  image                 String?
  logo                  String?
  cuisine               String?
  dynamicMenu           Boolean?       @default(false)
  wifiName              String?
  wifiPassword          String?
  softRestaurantVenueId String?
  tipPercentage1        String         @default("0.10")
  tipPercentage2        String         @default("0.15")
  tipPercentage3        String         @default("0.20")
  tipPercentages        Float[]        @default([0.10, 0.15, 0.20])
  askNameOrdering       Boolean?       @default(false)
  paymentMethods        String[]       @default(["card"])
  menta                 Menta?         @relation(fields: [mentaId], references: [id])
  mentaId               String?
  tpvs                  Tpv[]
  metrics               Metric[]
  shifts                Shift[]
  bills                 Bill[]
  billsV2               BillV2[]
  tables                Table[]
  users                 User[]         @relation("UserVenues")
  feedbacks             Feedback[]
  payments              Payment[]
  notifications         Notification[]
  menus                 Menu[]
  avoqadoMenus          AvoqadoMenu[]
  categories            Category[]

  avoqadoProducts   AvoqadoProduct[]
  products          Product[]
  languages         Language[]
  modifierGroups    ModifierGroup[]
  modifiers         Modifiers[]
  stripeAccountId   String?
  googleBusinessId  String?
  specialPayment    Boolean?         @default(false)
  specialPaymentRef String?          @unique
  configuration     Configuration?   @relation(fields: [configurationId], references: [id])
  configurationId   String?
  rewardProducts    RewardProduct[]
  userVenues        UserVenue[]
  waiters           Waiter[]
  color             Colors?          @relation(fields: [colorsId], references: [id])
  colorsId          String?
  updatedAt         DateTime?        @updatedAt
  createdAt         DateTime         @default(now())

  @@unique([posUniqueId])
}

model Menta {
  id          String  @id @default(cuid())
  merchantIdA String?
  merchantIdB String?
  venues      Venue[]
}

model Tpv {
  id            String    @id @default(cuid())
  idMenta       String?   @unique
  customerId    String?
  serial        String    @unique
  version       String?
  configuration String?
  tradeMark     String?
  model         String?
  status        String?
  name          String
  venueId       String
  payments      Payment[]
  venue         Venue     @relation(fields: [venueId], references: [id])
  updatedAt     DateTime? @updatedAt
  createdAt     DateTime  @default(now())

  @@index([venueId])
}

model Colors {
  id         String  @id @default(cuid())
  primary    String  @default("#fff")
  secondary  String
  tertiary   String
  quaternary String
  quinary    String
  senary     String
  septenary  String
  octonary   String
  nonary     String
  denary     String
  Venues     Venue[]
}

model Configuration {
  id               String     @id @default(cuid())
  primaryColor     String?
  secondaryColor   String?
  dynamicMenu      Boolean?   @default(false)
  branding         Boolean?   @default(false)
  posName          PosNames?
  stripeAccountId  String?
  tipPercentage1   String     @default("0.10")
  tipPercentage2   String     @default("0.15")
  tipPercentage3   String     @default("0.20")
  paymentMethods   String[]   @default(["card"])
  googleBusinessId String?
  specialPayment   Boolean?   @default(false)
  venues           Venue[]
  updatedAt        DateTime?  @updatedAt
  createdAt        DateTime   @default(now())
  languages        Language[]
}

model Language {
  id               String          @id @default(cuid())
  name             String
  code             String          @unique
  venue            Venue?          @relation(fields: [venueId], references: [id])
  venueId          String?
  configuration    Configuration?  @relation(fields: [configurationId], references: [id])
  configurationId  String?
  menus            Menu[]
  avoqadoMenu      AvoqadoMenu[]
  category         Category?       @relation(fields: [categoryId], references: [id])
  categoryId       String?
  avoqadoProduct   AvoqadoProduct? @relation(fields: [avoqadoProductId], references: [id])
  avoqadoProductId String?
}

enum PosNames {
  WANSOFT
  SOFTRESTAURANT
  NONE
}

//ANCHOR BILL
model Bill {
  id              String           @id @default(cuid())
  key             String?
  status          StatusBills?     @default(PENDING)
  billName        String?
  posOrder        Int?
  splitFromPos    Boolean?         @default(false)
  folio           String?
  tableNumber     Int?
  total           Decimal?
  splitType       SplitType?
  metrics         Metric[]
  table           Table[]
  products        Product[]
  userBills       UserBill[]
  waiterName      String?
  waiter          Waiter?          @relation(fields: [waiterId], references: [id])
  waiterId        String?
  orderedProducts OrderedProduct[]
  usertableId     String?
  payments        Payment[]
  tips            Tip[]
  venue           Venue?           @relation(fields: [venueId], references: [id])
  venueId         String?
  users           User[]
  qrCode          String?
  equalPartsId    String?
  shiftId         String?
  shift           Shift?           @relation(fields: [shiftId], references: [id])
  equalParts      EqualParts?      @relation(fields: [equalPartsId], references: [id])
  updatedAt       DateTime?        @updatedAt
  createdAt       DateTime         @default(now())

  @@index([venueId])
}

// se hizo para manejar la nueva version de endpoints para soft restaurant, donde no se hace tanta logica en el backend verificar mesas, si existen, sino desblindarlas etc. Es mas orientado al TPV no para el QR
model BillV2 {
  id    String  @id @default(cuid())
  key   String?
  folio String

  status StatusBills? @default(PENDING)

  billName                           String?
  tableName                          String?
  discount                           Int?
  total                              Decimal?         @default(0)
  amountLeft                         Decimal?         @default(0)
  orderFromPos                       Int?
  isSplittedFromPos                  Boolean?         @default(false)
  isRenamedFromPos                   Boolean?         @default(false)
  uniqueCodeFromPos                  String
  mainBillUniqueCodeForBillSplitting String? // Add this field to store the original bill's uniqueCode
  printed                            Boolean?         @default(false)
  payments                           Payment[]
  products                           Product[]
  splitType                          SplitType?
  isSplit                            Boolean?         @default(false)
  originalFolio                      String? // For split bills, reference to the original
  splitFolios                        String[] // For original bills, list of split folios
  shiftId                            String?
  shift                              Shift?           @relation(fields: [shiftId], references: [id])
  table                              Table[]
  waiterName                         String?
  waiter                             Waiter?          @relation(fields: [waiterId], references: [id])
  waiterId                           String?
  orderedProducts                    OrderedProduct[]
  usertableId                        String?
  tips                               Tip[]
  venue                              Venue?           @relation(fields: [venueId], references: [id])
  venueId                            String?
  users                              User[]
  qrCode                             String?
  equalPartsId                       String?
  equalParts                         EqualParts?      @relation(fields: [equalPartsId], references: [id])
  userBills                          UserBill[]
  metrics                            Metric[]
  updatedAt                          DateTime?        @updatedAt
  createdAt                          DateTime         @default(now())

  originalBillPayments PaymentAssignment[] @relation("OriginalBillPayments")
  currentBillPayments  PaymentAssignment[] @relation("CurrentBillPayments")

  @@unique([uniqueCodeFromPos, venueId])
  @@index([venueId])
}

model EqualParts {
  id            String    @id @default(cuid())
  peopleOnTable Int
  payedFor      Int
  total         Decimal
  amountLeft    Decimal
  bill          Bill[]
  billV2        BillV2[]
  updatedAt     DateTime? @updatedAt
  createdAt     DateTime  @default(now())
}

enum SplitType {
  PERPRODUCT
  EQUALPARTS
  CUSTOMAMOUNT
  FULLPAYMENT
}

model Menu {
  id          String     @id @default(cuid())
  name        String
  description String?
  visualOrder Int?
  imageUrl    String?
  active      Boolean    @default(true)
  isFixed     Boolean    @default(true) // Bandera para indicar si el menú está disponible todo el tiempo
  startTime   String? // Hora de inicio de disponibilidad
  endTime     String? // Hora de fin de disponibilidad
  categories  Category[]
  languageId  String?
  language    Language?  @relation(fields: [languageId], references: [id])
  venue       Venue?     @relation(fields: [venueId], references: [id])
  venueId     String?
  menuDays    MenuDay[] // Relation to the MenuDay model

  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())
}

enum DayOfWeek {
  MONDAY
  TUESDAY
  WEDNESDAY
  THURSDAY
  FRIDAY
  SATURDAY
  SUNDAY
}

enum ShiftOrigin {
  POS
  AVOQADO
  AVOQADO_TPV
}

model Shift {
  id           String       @id @default(cuid())
  turnId       Int
  insideTurnId Int?
  origin       ShiftOrigin? // Nuevo campo para distinguir el origen del turno
  startTime    String
  endTime      String?
  fund         Decimal?
  cash         Decimal?
  card         Decimal?
  credit       Decimal?
  fum          String?
  cashier      String?
  venue        Venue?       @relation(fields: [venueId], references: [id])
  bills        Bill[]
  billV2       BillV2[]
  payments     Payment[]
  venueId      String?
  active       Boolean      @default(true)
  updatedAt    DateTime?    @updatedAt
  createdAt    DateTime     @default(now())

  @@unique([turnId, venueId]) // Combinación única de venueId y idmesero
}

//ANCHOR AVOQADOMENU
model AvoqadoMenu {
  id            String     @id @default(cuid())
  name          String?
  shortDesc     String?
  longDesc      String?
  orderByNumber Int?       @default(0)
  active        Boolean    @default(true)
  isFixed       Boolean    @default(true) // Bandera para indicar si el menú está disponible todo el tiempo
  startTime     String? // Hora de inicio de disponibilidad
  startTimeV2   DateTime?
  endTime       String? // Hora de fin de disponibilidad
  endTimeV2     DateTime?
  categories    Category[]
  imageCover    String?
  languageId    String?
  language      Language?  @relation(fields: [languageId], references: [id])
  venueId       String
  venue         Venue      @relation(fields: [venueId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  menuDays      MenuDay[] // Relation to the MenuDay model
  updatedAt     DateTime?  @updatedAt
  createdAt     DateTime   @default(now())
}

model MenuDay {
  id          String        @id @default(cuid())
  day         DayOfWeek
  isFixed     Boolean       @default(true) // Bandera para indicar si el menú está disponible todo el tiempo
  startTime   String? // Hora de inicio de disponibilidad
  startTimeV2 DateTime?
  endTime     String? // Hora de fin de disponibilidad
  endTimeV2   DateTime?
  avoqadoMenu AvoqadoMenu[]
  createdAt   DateTime      @default(now())
  menu        Menu?         @relation(fields: [menuId], references: [id])
  menuId      String?
}

model Role {
  id          String       @id @unique @default(cuid())
  name        String       @unique
  users       User[]
  permissions Permission[]
  updatedAt   DateTime?    @updatedAt
  createdAt   DateTime     @default(now())
}

model PermissionType {
  id          String       @id @default(cuid())
  name        String
  permissions Permission[]
  updatedAt   DateTime?    @updatedAt
  createdAt   DateTime     @default(now())
}

model Permission {
  id               String          @id @unique @default(cuid())
  name             String          @unique
  roleId           String?
  roles            Role?           @relation(fields: [roleId], references: [id])
  permissiontypeId String?
  permissiontype   PermissionType? @relation(fields: [permissiontypeId], references: [id])
  updatedAt        DateTime?       @updatedAt
  createdAt        DateTime        @default(now())
}

//ANCHOR USER
model User {
  id                 String              @id @default(cuid())
  role               RoleEnumType?       @default(USER)
  email              String?             @unique
  name               String?
  isVerifiedEmail    Boolean?            @default(false)
  emailToken         String?
  token              String?
  fcmToken           String?
  username           String?             @unique
  bill               Bill?               @relation(fields: [billId], references: [id])
  billId             String?
  allergens          Allergens[]
  userProductRewards UserProductReward[]
  userBills          UserBill[]
  image              String?
  phone              String?
  googleId           String?             @unique
  refreshToken       String?
  venues             Venue[]             @relation("UserVenues")

  //TODO create-> deviceToken       String?
  password String?
  color    String?
  tips     Tip[]
  paid     Decimal?
  roleId   String?

  stripeCustomerId String?
  roles            Role?        @relation(fields: [roleId], references: [id])
  cardTokens       CardTokens[]
  venueId          String?

  payments          Payment[]
  sessions          Session[]
  preferencesclient PreferenceClient[]
  availabilities    Availabilities[]
  Password          Password?
  chain             Chain?             @relation(fields: [chainId], references: [id])
  chainId           String?
  userVenues        UserVenue[]
  userWaiters       UserWaiter[]
  updatedAt         DateTime?          @updatedAt
  createdAt         DateTime           @default(now())
  BillV2            BillV2?            @relation(fields: [billV2Id], references: [id])
  billV2Id          String?
}

model UserBill {
  id       String  @id @default(cuid())
  userId   String
  user     User    @relation(fields: [userId], references: [id])
  billId   String
  bill     Bill    @relation(fields: [billId], references: [id], onDelete: Cascade)
  BillV2   BillV2? @relation(fields: [billV2Id], references: [id])
  billV2Id String?

  @@unique([userId, billId]) // Para evitar duplicados en la relación
}

model UserVenue {
  id      String       @id @default(cuid())
  userId  String
  venueId String
  role    RoleEnumType @default(USER) // New field for per-venue roles
  user    User         @relation(fields: [userId], references: [id])
  venue   Venue        @relation(fields: [venueId], references: [id])

  @@unique([userId, venueId])
}

model Waiter {
  id            String        @id @default(uuid())
  idmesero      String
  nombre        String
  captain       Boolean       @default(false)
  pin           String?
  payments      Payment[]
  venueId       String
  venue         Venue         @relation(fields: [venueId], references: [id])
  bills         Bill[]
  billsV2       BillV2[]
  products      Product[]
  tips          Tip[]
  feedbacks     Feedback[]
  userWaiters   UserWaiter[]
  waiterMetrics WaiterMetric?
  updatedAt     DateTime?     @updatedAt
  createdAt     DateTime      @default(now())

  @@unique([venueId, idmesero])
  @@unique([venueId, pin])
  @@index([venueId])
}

model UserWaiter {
  id       String  @id @default(cuid())
  userId   String
  waiterId String
  pin      String?

  // Relaciones
  user   User   @relation(fields: [userId], references: [id])
  waiter Waiter @relation(fields: [waiterId], references: [id], onDelete: Cascade)

  @@unique([userId, waiterId])
}

model WaiterMetric {
  id               String  @id @default(uuid())
  waiterId         String  @unique
  totalSales       Decimal @default(0)
  totalTips        Decimal @default(0)
  numberOfBills    Int     @default(0)
  averageBill      Decimal @default(0)
  performanceScore Float   @default(0) // Podrías definir una lógica para calcular esto

  // Relaciones
  waiter Waiter @relation(fields: [waiterId], references: [id])

  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt
}

// Modelo para guardar la información de las promociones de productos
model RewardProduct {
  id             String @id @default(cuid())
  productName    String // Nombre del producto en promoción
  productKey     String @unique // Clave única del producto para identificarlo
  quantityNeeded Int // Cantidad necesaria para obtener el premio

  type    RewardType?
  reward  String // Descripción del premio (e.g., "Octavo café gratis")
  venueId String
  venue   Venue       @relation(fields: [venueId], references: [id])

  userProductReward UserProductReward[]
  products          Product[]
  createdAt         DateTime            @default(now())
  updatedAt         DateTime            @updatedAt
}

enum RewardType {
  COFFEE
  MEAL
  DRINK
  OTHER
}

model Invitation {
  id        String        @id @default(cuid()) // Identificador único para cada invitación
  role      RoleEnumType? // Rol que se le asignará al usuario invitado
  email     String // Correo electrónico del usuario invitado
  token     String        @unique // Token de invitación único
  expiresAt DateTime // Fecha de expiración del token
  isUsed    Boolean       @default(false) // Estado para saber si el token ya fue usado
  createdAt DateTime      @default(now()) // Fecha de creación de la invitación
  updatedAt DateTime      @updatedAt // Fecha de última actualización
}

// Modelo para registrar el progreso de cada usuario hacia la obtención de un producto gratuito
model UserProductReward {
  id              String        @id @default(cuid())
  userId          String
  user            User          @relation(fields: [userId], references: [id])
  rewardProductId String
  rewardProduct   RewardProduct @relation(fields: [rewardProductId], references: [id])
  orderedCount    Int           @default(0) // Cantidad de productos ordenados por el usuario
  createdAt       DateTime      @default(now())
  updatedAt       DateTime      @updatedAt
}

model CardTokens {
  id        String           @id @default(cuid())
  processor PaymentProcessor
  token     String           @unique
  user      User?            @relation(fields: [userId], references: [id])
  userId    String?
  updatedAt DateTime?        @updatedAt
  createdAt DateTime         @default(now())
}

enum PaymentProcessor {
  STRIPE
  MERCADO_PAGO
  PAYPAL
  BLUMONPAY
  // Add other payment processors as needed
}

// model User{
//   @@map(name: "users")

//   id String  @id @default(uuid())
//   name String  @db.VarChar(255)
//   email String @unique
//   photo String? @default("default.png")
//   verified Boolean? @default(false) 

//   password String
//   role RoleEnumType? @default(user)

//   verificationCode String? @db.Text @unique

//   createdAt DateTime @default(now())
//   updatedAt DateTime @updatedAt

//   @@unique([email, verificationCode])
//   @@index([email, verificationCode])
// }

enum RoleEnumType {
  USER
  ADMIN
  SUPERADMIN
  VENUEADMIN
  WAITER
}

model Allergens {
  id               String          @id @default(cuid())
  name             String
  updatedAt        DateTime?       @updatedAt
  createdAt        DateTime        @default(now())
  user             User?           @relation(fields: [userId], references: [id])
  userId           String?
  avoqadoProduct   AvoqadoProduct? @relation(fields: [avoqadoProductId], references: [id])
  avoqadoProductId String?
}

// model Session {
//   id             String    @id @default(uuid())
//   active         Boolean   @default(true)
//   user           User      @relation(fields: [userId], references: [id], onDelete: Cascade)
//   userId         String
//   expirationDate DateTime
//   updatedAt      DateTime? @updatedAt
//   createdAt      DateTime  @default(now())
// }

model Password {
  id        String    @id @default(cuid())
  hash      String
  user      User?     @relation(fields: [userId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  userId    String?   @unique
  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())
}

model Table {
  tableNumber Int
  status      StatusTable? @default(INACTIVE)
  count       Int?         @default(0)
  venue       Venue        @relation(fields: [venueId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  venueId     String
  bill        Bill?        @relation(fields: [billId], references: [id])
  payments    Payment[]
  billId      String?      @unique
  qrs         Qr[]
  floorId     String?
  locationId  String?
  seats       Int?
  demo        Boolean?     @default(false)
  metrics     Metric[]
  // usertables  UserTable[]
  billV2      BillV2?      @relation(fields: [billV2Id], references: [id])
  billV2Id    String?

  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())

  @@id(name: "tableId", [tableNumber, venueId])
  @@index([venueId])
}

model Qr {
  id          String    @id @default(cuid())
  name        String
  active      Boolean   @default(true)
  qr          String
  tableNumber Int
  venueId     String
  table       Table     @relation(fields: [tableNumber, venueId], references: [tableNumber, venueId])
  updatedAt   DateTime? @updatedAt
  createdAt   DateTime  @default(now())
}

model Category {
  id              String           @id @default(cuid())
  name            String
  image           String?
  displayBill     Int?
  description     String?
  orderByNumber   Int?             @default(0)
  color           String?
  pdf             Boolean?         @default(false)
  active          Boolean?         @default(true)
  availabilities  Availabilities[]
  menu            Menu?            @relation(fields: [menuId], references: [id])
  menuId          String?
  venueId         String?
  venue           Venue?           @relation(fields: [venueId], references: [id])
  avoqadoMenus    AvoqadoMenu[]
  // avoqadoMenuId   String?
  avoqadoProducts AvoqadoProduct[]
  languages       Language[]
  updatedAt       DateTime?        @updatedAt
  createdAt       DateTime         @default(now())
}

model AvoqadoProduct {
  id             String           @id @default(cuid())
  sku            String?
  key            String?
  type           ProductType?
  name           String
  imageUrl       String?
  quantityUnit   String?
  description    String?
  orderByNumber  Int?
  sortOrder      Int?
  instagramUrl   String?
  price          Decimal
  active         Boolean?         @default(true)
  modifierGroups ModifierGroup[]
  categories     Category[]
  calories       Int?
  allergens      Allergens[]
  languages      Language[]
  venueId        String?
  venue          Venue?           @relation(fields: [venueId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  availabilities Availabilities[]
  tags           Tag[]
  updatedAt      DateTime?        @updatedAt
  createdAt      DateTime         @default(now())

  @@unique([venueId, sortOrder])
  // Compound unique so that orderByNumber is unique only within a given venue
  @@index([venueId])
}

model Tag {
  id              String           @id @default(cuid())
  name            String
  updatedAt       DateTime?        @updatedAt
  createdAt       DateTime         @default(now())
  avoqadoProducts AvoqadoProduct[]
}

model Product {
  id                String       @id @default(cuid())
  idproducto        String?
  key               String?      @unique // Clave única del producto para identificarlo
  name              String
  sequence          String?
  quantity          Decimal?     @default(1)
  modifier          Int?         @default(0)
  status            String?
  paid              Boolean?     @default(false)
  type              ProductType?
  posOrder          Int?
  uniqueCodeFromPos String?
  cost              Decimal?
  punitario         Decimal?
  tax               Decimal?
  price             Decimal
  discount          Decimal?
  byAvoqado         Boolean?     @default(false)
  productType       Int? //Products 1, Modifiers 2, Modifier Groups 3, and Bundles 4
  available         Boolean?
  billId            String?
  bill              Bill?        @relation(fields: [billId], references: [id], onUpdate: Cascade)
  billV2            BillV2?      @relation(fields: [billV2Id], references: [id])
  billV2Id          String?

  paymentId        String?
  payment          Payment?          @relation(fields: [paymentId], references: [id])
  waiterName       String?
  waiter           Waiter?           @relation(fields: [waiterId], references: [id])
  waiterId         String?
  combodetails     ComboDetails[]
  availabilities   Availabilities[]
  venue            Venue?            @relation(fields: [venueId], references: [id])
  venueId          String?
  productModifiers ProductModifier[]
  rewardProductId  String?
  rewardProduct    RewardProduct?    @relation(fields: [rewardProductId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  orderedProducts  OrderedProduct[]
  updatedAt        DateTime?         @updatedAt
  createdAt        DateTime          @default(now())

  @@unique([uniqueCodeFromPos, venueId])
  @@index([billV2Id])
  @@index([venueId])
}

enum ProductType {
  BEVERAGE
  FOOD
  OTHER
  MODIFIER
  MERCH
  MEMBERSHIP
  SESSION
  UNKNOWN
}

model OrderedProduct {
  id                  String       @id @default(cuid())
  quantity            Int          @default(1)
  orderedBy           String?
  isPaid              Boolean?     @default(false)
  type                ProductType?
  unitPrice           Decimal // The price of a single unit of the product at the time of the order
  totalPrice          Decimal // Total price (unitPrice * quantity + modifiers)
  specialInstructions String?
  productId           String

  product          Product           @relation(fields: [productId], references: [id])
  productModifiers ProductModifier[]
  modifiers        Modifiers[] // Any modifiers for the product (e.g., extra cheese)
  billId           String?
  bill             Bill?             @relation(fields: [billId], references: [id], onDelete: Cascade)
  billV2           BillV2?           @relation(fields: [billV2Id], references: [id])
  billV2Id         String?

  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt
}

model Availabilities {
  id               String          @id @default(cuid())
  dayOfWeek        Int?
  startTime        String?
  endTime          String?
  productId        String?
  product          Product?        @relation(fields: [productId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  avoqadoProductId String?
  avoqadoProduct   AvoqadoProduct? @relation(fields: [avoqadoProductId], references: [id], onDelete: Cascade, onUpdate: Cascade)
  category         Category?       @relation(fields: [categoryId], references: [id])
  categoryId       String?
  user             User?           @relation(fields: [userId], references: [id])
  userId           String?
  updatedAt        DateTime?       @updatedAt
  createdAt        DateTime        @default(now())
}

model Payment {
  id                          String          @id @default(cuid())
  method                      PaymentMethod?
  methodString                String? // Temporary field for migration
  status                      StatusPayments? @default(PENDING)
  amount                      Decimal
  source                      PaymentSource?
  splitType                   SplitType?
  cardBrand                   String?
  reference                   String?
  //TPV config
  token                       String?         @unique
  typeOfCard                  String?
  tpvId                       String?
  tpv                         Tpv?            @relation(fields: [tpvId], references: [id])
  mentaAuthorizationReference String?
  mentaOperationId            String?
  mentaTicketId               String?
  //END TPV config
  bank                        String?
  currency                    String?
  equalPartsPartySize         Int?
  equalPartsPayedFor          Int?
  stripePaymentIntentId       String?
  waiterName                  String?
  shift                       Shift?          @relation(fields: [shiftId], references: [id])
  shiftId                     String?
  waiter                      Waiter?         @relation(fields: [waiterId], references: [id])
  waiterId                    String?
  tableNumber                 Int?
  table                       Table?          @relation(fields: [tableNumber, venueId], references: [tableNumber, venueId])
  last4                       String?
  userFee                     Decimal?
  avoFee                      Decimal?
  receiptUrl                  String?
  customerId                  String?
  cardCountry                 String?
  products                    Product[]
  tips                        Tip[]
  billId                      String?
  bill                        Bill?           @relation(fields: [billId], references: [id], onDelete: Cascade)
  billV2                      BillV2?         @relation(fields: [billV2Id], references: [id])
  billV2Id                    String?

  userId            String?
  user              User?               @relation(fields: [userId], references: [id])
  anonymousUser     String?
  cardDetails       CardDetails[]
  notifications     Notification[]
  venue             Venue?              @relation(fields: [venueId], references: [id])
  venueId           String?
  updatedAt         DateTime?           @updatedAt
  createdAt         DateTime            @default(now())
  PaymentAssignment PaymentAssignment[]

  @@index([billId])
  @@index([userId])
  @@index([venueId])
}

// Add to your Prisma schema
model PaymentAssignment {
  id             String   @id @default(uuid())
  paymentId      String
  originalBillId String // Always the first bill the payment was associated with
  currentBillId  String // The bill this portion of the payment is currently associated with
  amount         Decimal  @db.Decimal(10, 2) // Amount assigned to this specific bill
  createdAt      DateTime @default(now())
  updatedAt      DateTime @updatedAt
  isReassigned   Boolean  @default(false) // Flag to track if this assignment has been moved
  splitOperation String? // Optional reference to track which split operation caused this reassignment

  payment      Payment @relation(fields: [paymentId], references: [id])
  originalBill BillV2  @relation("OriginalBillPayments", fields: [originalBillId], references: [id])
  currentBill  BillV2  @relation("CurrentBillPayments", fields: [currentBillId], references: [id])

  @@unique([paymentId, currentBillId, isReassigned])
  @@index([originalBillId])
  @@index([currentBillId])
  @@index([paymentId])
}

enum CardType {
  CREDIT
  DEBIT
  PREPAID
  UNKNOWN
}

enum CardNetwork {
  VISA
  MASTERCARD
  AMEX
  DISCOVER
  UNIONPAY
  JCB
  DINERS
  MAESTRO
  OTHER
}

model CardDetails {
  id              String       @id @default(cuid())
  cardType        CardType     @default(UNKNOWN)
  cardNetwork     CardNetwork? // Visa, Mastercard, etc.
  cardBrand       String? // Nombre específico del producto (Platinum, Gold, etc.)
  last4           String? // Últimos 4 dígitos
  cardCountry     String? // País de emisión
  expiryMonth     Int? // Mes de expiración 
  expiryYear      Int? // Año de expiración
  issuerBank      String? // Banco emisor
  isInternational Boolean      @default(false)

  // Campos para transacciones tokenizadas
  tokenId     String?
  isTokenized Boolean @default(false)

  // Información adicional que pueda ser útil para análisis
  cardCategory String? // Consumer, Business, etc.

  payment Payment? @relation(fields: [paymentId], references: [id])

  createdAt DateTime  @default(now())
  updatedAt DateTime? @updatedAt
  paymentId String?
}

enum PaymentSource {
  AVOQADO
  POS
  AVOQADO_TPV
  SOFTRESTAURANT
  TPV //REMOVE LATER
}

model Paymentbeforesendingqueue {
  paymentid String @id @map("paymentid")
  jsondata  Json   @map("jsondata")

  @@map("paymentbeforesendingqueue")
}

model Paymentaftersendingqueue {
  datetimesent DateTime @map("datetimesent")
  billid       String   @map("billid")
  paymentid    String   @map("paymentid")
  tablenumber  Int?     @map("tablenumber")

  @@id([datetimesent, paymentid])
  @@map("paymentaftersendingqueue")
}

model Paymenterrorsvalidationqueue {
  paymentid String @id @map("paymentid")
  errors    Json   @map("errors")

  @@map("paymenterrorsvalidationqueue")
}

model Tip {
  id     String         @id @default(cuid())
  amount Decimal
  source PaymentSource?

  percentage Decimal
  billId     String?
  bill       Bill?    @relation(fields: [billId], references: [id], onDelete: Cascade)
  payment    Payment? @relation(fields: [paymentId], references: [id])
  paymentId  String?
  users      User[]
  waiter     Waiter?  @relation(fields: [waiterId], references: [id])
  waiterId   String?
  billV2     BillV2?  @relation(fields: [billV2Id], references: [id])
  billV2Id   String?

  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())
}

//NOTE - MODIFIY
model Notification {
  id        String              @id @default(cuid())
  method    NotificationMethod?
  type      String?
  type_temp NotificationType?
  status    StatusNotification?
  message   String?
  venueId   String?
  venue     Venue?              @relation(fields: [venueId], references: [id])

  // billId      String?
  // bill        Bill?               @relation(fields: [billId], references: [id])
  paymentId String?
  payment   Payment?  @relation(fields: [paymentId], references: [id])
  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())
}

model Combo {
  id           String         @id @default(cuid())
  name         String?
  dateFrom     DateTime?
  dateTo       DateTime?
  active       Boolean?
  combodetails ComboDetails[]
  updatedAt    DateTime?      @updatedAt
  createdAt    DateTime       @default(now())
}

model ComboDetails {
  id        String    @id @default(cuid())
  price     Float?
  quantity  Int?
  active    Boolean?
  productId String?
  product   Product?  @relation(fields: [productId], references: [id])
  comoboId  String?
  combo     Combo?    @relation(fields: [comoboId], references: [id])
  updatedAt DateTime? @updatedAt
  createdAt DateTime  @default(now())
}

model ErrorLog {
  id               String   @id @default(uuid())
  requestId        String // Correlation ID for request tracking
  endpoint         String // API endpoint that generated the error
  errorMessage     String // Short error message
  errorStack       String?  @db.Text // Full error stack trace
  processingTimeMs Int // How long the request ran before failing (ms)
  errorTime        DateTime @default(now()) // When the error occurred
  requestPayload   String?  @db.Text // Truncated or sanitized request payload
  resolved         Boolean  @default(false) // Flag for tracking error resolution
  notes            String?  @db.Text // Optional notes for support/development team

  // Add appropriate indexes for efficient querying
  @@index([requestId])
  @@index([errorTime])
  @@index([endpoint, errorTime])
  @@index([resolved, errorTime]) // For finding open/unresolved errors
}

model Feedback {
  id           String    @id @default(cuid())
  description  String?
  stars        Int?
  food         Int?
  service      Int?
  atmosphere   Int?
  priceQuality Int?
  userId       String?
  venue        Venue?    @relation(fields: [venueId], references: [id])
  venueId      String?
  waiter       Waiter?   @relation(fields: [waiterId], references: [id])
  waiterId     String?
  updatedAt    DateTime? @updatedAt
  createdAt    DateTime  @default(now())

  @@index([venueId])
}

model PreferenceClient {
  id             String          @id @default(cuid())
  description    String?
  active         Boolean?
  preferencetype PreferenceType?
  userId         String?
  user           User?           @relation(fields: [userId], references: [id])
  updatedAt      DateTime?       @updatedAt
  createdAt      DateTime        @default(now())
}

model ModifierGroup {
  id                      String           @id @default(cuid())
  name                    String?
  available               Boolean?         @default(true)
  plu                     String?          @unique
  required                Boolean?         @default(false)
  min                     Int?             @default(0)
  max                     Int?             @default(1)
  multipleSelectionAmount Int?             @default(0)
  multiMax                Int?             @default(1)
  multiply                Int?             @default(0)
  active                  Boolean?         @default(true)
  modifiers               Modifiers[]
  avoqadoProducts         AvoqadoProduct[]
  venue                   Venue?           @relation(fields: [venueId], references: [id])
  venueId                 String?
  updatedAt               DateTime?        @updatedAt
  createdAt               DateTime         @default(now())
}

model Modifiers {
  id         String   @id @default(cuid())
  name       String?
  available  Boolean? @default(true)
  plu        String?  @unique
  max        Int?     @default(0)
  min        Int?     @default(0)
  multiply   Int?     @default(0)
  extraPrice Decimal?
  active     Boolean? @default(true)

  modifierGroups   ModifierGroup[]
  productmodifiers ProductModifier[]
  venue            Venue?            @relation(fields: [venueId], references: [id])
  venueId          String?
  orderedProductId String?
  orderedProduct   OrderedProduct?   @relation(fields: [orderedProductId], references: [id])
  updatedAt        DateTime?         @updatedAt
  createdAt        DateTime          @default(now())
}

model ProductModifier {
  id               String          @id @default(cuid())
  name             String?
  quantity         Int?            @default(0)
  plu              String?         @unique
  extraPrice       Decimal?
  total            Decimal?
  product          Product?        @relation(fields: [productId], references: [id])
  productId        String?
  orderedProduct   OrderedProduct? @relation(fields: [orderedProductId], references: [id])
  orderedProductId String?
  modifiers        Modifiers?      @relation(fields: [modifierId], references: [id])
  modifierId       String?
  updatedAt        DateTime?       @updatedAt
  createdAt        DateTime        @default(now())
}

enum PreferenceType {
  dietary_restrictions
  dishes
  payment_methods
}

enum NotificationMethod {
  email
  sms
  push
  whatsapp
}

enum NotificationType {
  CALL
  ORDER
  PAYMENT
  FEEDBACK
  INFORMATIVE
  OTHER
}

enum PaymentMethod {
  CASH
  CARD
  STRIPE
  AMEX
  VISA
  MC
  TERMINAL
  OTHER
  VALES
}

enum StatusPayments {
  ACCEPTED
  REJECTED
  PENDING
  REFUNDED
}

enum StatusBills {
  OPEN
  PAID
  PENDING
  CLOSED
  CANCELED
  PRECREATED
  WITHOUT_TABLE
  DELETED
  EARLYACCESS
  COURTESY
}

enum StatusNotification {
  pending
  rejected
  sending
}

enum StatusTable {
  ACTIVE
  INACTIVE
}
