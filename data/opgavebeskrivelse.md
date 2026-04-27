Dagens opgave
Opgave: AI-vurdering af en opgave ud fra en rubric
I denne opgave skal I bygge en lille AI-drevet applikation, som kan give en første vurdering af en studenteropgave ved hjælp af en rubric og et kald til et eksternt LLM-API.

Formålet er at arbejde praktisk med, hvordan man integrerer en sprogmodel i sin egen software. Fokus er derfor ikke kun på at få et svar fra modellen, men på at designe en lille applikation, hvor en LLM indgår som en del af et samlet dataflow.

Baggrund
I får udleveret:

et eller flere vurderingsoplæg i markdown, som beskriver krav og forventninger til den type opgave, der skal vurderes
en eksempelopgave i markdown, som kan bruges som testinput til jeres løsning
Vurderingsoplægget skal I bruge som grundlag for at udvikle en rubric, som jeres applikation derefter anvender til at vurdere eksempelopgaven.

Materialer (kilder)
Læringsmål. De officielle krav.
Krav til rapport. Vejledning til hvad rapporten skal indeholde.
Dare, Share, Care. Beskrivelse af konceptet Dare-Share-Care.
Student1. Eksempel 1 på en opgave, som skal vurderes.
Student2. Eksempel 2 på en opgave, som skal vurderes.
Student3. Eksempel 3 på en opgave, som skal vurderes.
Opgavens idé
I skal bygge en applikation, hvor en bruger kan indsende en opgavetekst og få en AI-genereret vurdering tilbage.

Applikationen skal som minimum kunne:

modtage en opgavetekst
anvende en rubric, som er udledt af vurderingsoplægget
sende relevante prompts til et LLM via API
modtage et svar fra modellen
returnere en struktureret vurdering med feedback
Det er vigtigt, at jeres løsning ikke præsenteres som en “automatisk sand bedømmelse”, men som en vejledende AI-baseret vurdering, der tager udgangspunkt i de kriterier, I har formuleret.

Delopgaver
1. Udled en rubric
Læs vurderingsoplægget og omsæt det til en rubric, som kan bruges i jeres applikation.

Jeres rubric bør som minimum indeholde:

4–6 vurderingskriterier
en kort beskrivelse af hvert kriterium
en beskrivelse af, hvad der kendetegner henholdsvis lav, middel og høj målopfyldelse
eventuelt en vægtning, hvis det giver mening
Rubric’en må gerne formuleres som almindelig tekst, men det vil være en fordel, hvis I strukturerer den, så den er let at bruge i jeres kode, fx som JSON eller en Java-model.

2. Design jeres prompts
I skal udforme mindst:

en systemprompt, der forklarer modellens rolle, opgaven og kravene til svaret
en userprompt, der indeholder rubric og opgavetekst
Overvej blandt andet:

Hvilken rolle skal modellen have?
Hvordan undgår I generiske eller uklare svar?
Hvordan sikrer I, at modellen holder sig til rubricen?
Hvordan beder I modellen om at returnere et svar i et format, som jeres applikation kan bruge?
3. Byg backend med API-kald
I skal bygge en backend, som kalder et eksternt LLM-API.

Backenden skal som minimum:

modtage input fra en klient
bygge requesten til LLM’et
sende requesten til API’et
modtage svaret
returnere et resultat til klienten
I vælger selv, hvordan jeres endpoint(s) skal se ud, men løsningen skal være tydelig og gennemskuelig.

4. Returnér struktureret feedback
Jeres applikation skal returnere en vurdering, der er mere struktureret end blot en løs tekstblok.

Vurderingen bør fx indeholde:

en samlet vurdering
feedback pr. kriterium
styrker
svagheder
forslag til forbedringer
Forslag til spørgmål (4-6 stk) til videre dialog med den studerende
Det er en fordel, hvis svaret returneres som JSON.

5. Test og reflektér
Afprøv jeres løsning på den eksempelopgave, I har fået udleveret.

Reflektér over spørgsmål som:

Gav rubricen en brugbar vurdering?
Gav modellen stabile og relevante svar?
Hvor var vurderingen stærk?
Hvor var den usikker eller misvisende?
Hvilke begrænsninger er der ved at bruge AI til denne type vurdering?
Minimumskrav
Jeres løsning skal som minimum indeholde:

en rubric udledt af vurderingsoplægget
et fungerende backend-kald til et LLM-API
mindst ét endpoint i jeres egen applikation
en test med den udleverede eksempelopgave
struktureret feedback som output
kort dokumentation af jeres løsning og jeres vigtigste valg
Mulige udvidelser
Hvis I bliver hurtigt færdige, kan I udvide løsningen med fx:

en simpel frontend i React
mulighed for at indsætte eller uploade tekst
flere forskellige rubrics
mulighed for at vælge vurderingsniveau eller feedbackstil
bedre fejlhåndtering ved timeout eller API-fejl
lagring af vurderinger i database
sammenligning af flere prompts eller modeller
Teknisk afgrænsning
For at holde fokus på dagens tema anbefales det, at I ikke starter med PDF-upload. Arbejd i stedet med opgavetekst og vurderingsoplæg i markdown eller plain text.

Fokus i opgaven er:

LLM API-integration
promptdesign
struktureret output
backend-logik
refleksion over kvalitet og begrænsninger
Hvad vi kigger efter
Vi kigger ikke efter en perfekt eller “færdig” vurderingsmotor. Vi kigger efter, om I kan:

omsætte kravmateriale til en brugbar rubric
integrere et LLM-API i jeres egen applikation
designe gode prompts
håndtere output på en fornuftig måde
reflektere kritisk over styrker og svagheder ved løsningen
Forslag til afleverings- eller portfolioindhold
I jeres portfolio kan I fx dokumentere:

jeres rubric
jeres systemprompt og userprompt
jeres endpoint-design
eksempel på request/response
korte refleksioner over, hvad der virkede godt og dårligt
hvilke forbedringer I ville lave i næste version
Kort opsummering
I skal altså bygge en lille AI-drevet vurderingsapplikation, hvor:

input er en opgavetekst
grundlaget er en rubric, som I selv udleder fra vurderingsoplægget
motoren er et eksternt LLM-API
output er en struktureret, vejledende vurdering
Målet er at få praktisk erfaring med, hvordan man gør en sprogmodel til en del af en rigtig softwareapplikation.