import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dimension
import java.io.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

val mainColor = Color.Gray

lateinit var text: MutableState<String>
lateinit var resText: MutableState<String>
lateinit var fileName: MutableState<String>
lateinit var fileOutputName: MutableState<String>

lateinit var p: MutableState<Int>
lateinit var k: MutableState<Int>
lateinit var x: MutableState<Int>
lateinit var g: MutableState<Int>
lateinit var y: MutableState<Int>

var srcByteList: ByteArray = byteArrayOf()
var resByteList: ByteArray = byteArrayOf()

@Composable
@Preview
fun App() {

    text = remember {mutableStateOf("")}
    resText = remember {mutableStateOf("")}
    fileName = remember {mutableStateOf("")}
    fileOutputName = remember {mutableStateOf("")}


    p = remember { mutableStateOf(0) }
    k = remember { mutableStateOf(0) }
    x = remember { mutableStateOf(0) }
    g = remember { mutableStateOf(0) }
    y = remember { mutableStateOf(0) }

    MaterialTheme {
        Column {
            Header("Лабораторная №3: Криптосистема Эль-Гамаля")

            Row(modifier = Modifier.fillMaxSize()){
                inputDataSection(

                    modifier = Modifier.fillMaxHeight().weight(1f)
                )
                resultField(

                    modifier = Modifier.fillMaxHeight().weight(1f).background(Color.LightGray)
                )
            }


        }

    }
}

@Composable
fun resultField(modifier: Modifier) {

    Column(verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = resText.value,
            onValueChange = {},
            label = {Text("Зашифрованный или расшифрованный текст", fontSize=10.sp) },
            textStyle = TextStyle(fontSize=14.sp),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState(0)),
            readOnly = true
        )

        Row (Modifier.height(85.dp), verticalAlignment = Alignment.CenterVertically){
            InputString(fileOutputName, "Введите путь к файлу", modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .height(55.dp).weight(1f)
                .verticalScroll(rememberScrollState(0)))
            Button(
                enabled = fileOutputName.value.isNotEmpty() && resText.value.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(mainColor),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).height(55.dp),
                onClick = {
                    if (fileOutputName.value!="") {
                        try {
                            val fos = FileOutputStream(fileOutputName.value)
                            val dos = DataOutputStream(fos)
                            dos.write(resByteList)
                            dos.close()
                            fos.close()
                        } catch(_: Exception){

                        }
                        finally {

                        }
                    }
                }
            ) {
                Text("Сохранить", color = Color(0xffffffff))
            }
        }
    }
}



@Composable
fun inputDataSection(modifier: Modifier){
    var isKeyCorrect by remember { mutableStateOf(true) }
    Column (verticalArrangement = Arrangement.SpaceBetween, modifier = modifier){
        Column (verticalArrangement = Arrangement.SpaceEvenly){
            isKeyCorrect = KeyInput()

            Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){
                InputString(fileName, "Введите путь к файлу", modifier = Modifier.padding(all = 10.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .height(55.dp).weight(1f)
                    .verticalScroll(rememberScrollState(0)))
                Button(
                    enabled = fileName.value.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(mainColor),
                    modifier = Modifier.padding(10.dp).height(55.dp),
                    onClick = {
                        if (fileName.value!="") {
                            try {
                                val fis = FileInputStream(fileName.value)
                                val dis = DataInputStream(fis)
                                srcByteList = ByteArray(fis.available())
                                dis.read(srcByteList)
                                text.value = byteListToString(srcByteList)
                                dis.close()
                                fis.close()
                            } catch(_: Exception){

                            }
                            finally {

                            }
                        }
                    }
                ) {
                    Text("Заполнить", color = Color(0xffffffff))
                }
            }


            TextField(
                value = text.value,
                onValueChange = {},
                label = {Text("Исходный или зашифрованный текст", fontSize=10.sp) },
                textStyle = TextStyle(fontSize=14.sp),
                modifier = Modifier.padding(horizontal = 10.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState(0)),
                readOnly = true
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Button(
                enabled = srcByteList.isNotEmpty() && isKeyCorrect,
                colors = ButtonDefaults.buttonColors(mainColor),
                modifier = Modifier.padding(15.dp),
                onClick = {
                    Shifr()
                }
            ) {
                Text("Шифрование", color = Color(0xffffffff))
            }

            Button(
                enabled = srcByteList.isNotEmpty() && isKeyCorrect && srcByteList.size%8==0,
                colors = ButtonDefaults.buttonColors(mainColor),
                modifier = Modifier.padding(15.dp),
                onClick = {
                    Deshifr()
                }
            ) {
                Text("Дешифрование", color = Color(0xffffffff))
            }
        }

    }
}

fun Shifr(){
    if (srcByteList.isNotEmpty()){
        var bo = ByteArrayOutputStream(srcByteList.size*8)
        val bw = DataOutputStream(bo)
        val a = power(g.value, k.value, p.value)

        for (m in srcByteList){
            val b = ((power(y.value, k.value, p.value).toUInt() * m.toUByte()) % p.value.toUInt()).toInt()

            bw.writeInt(a)
            bw.writeInt(b)

        }

        bw.flush()
        resByteList = bo.toByteArray()
        bo.flush()
        resText.value = resByteListToString(resByteList)
    }
}

fun Deshifr(){
    if (srcByteList.isNotEmpty()){
        var bi = ByteArrayInputStream(srcByteList)
        var reader = DataInputStream(bi)

        var bo = ByteArrayOutputStream(srcByteList.size*8)
        val bw = DataOutputStream(bo)


        for (b in 1..srcByteList.size/(4*2)){
            val a = reader.readInt()
            val b = reader.readInt()

            val m = ((b*power(a,p.value-1-x.value,p.value))%p.value)

            bw.writeByte(m)
        }

        bw.flush()
        resByteList = bo.toByteArray()

        bw.close()
        bo.close()

        reader.close()
        bi.close()

        resText.value = byteListToString(resByteList)
    }
}

fun resByteListToString(b:ByteArray): String{
    var bi = ByteArrayInputStream(b)
    var reader = DataInputStream(bi)
    val sb = StringBuilder()

    if (b.size<=20*4*2){
        sb.append("Длина файла меньше либо равна 20 парам чисел: ")
        for (i in 1..b.size/8) {
            sb.append('{')
            sb.append(reader.readInt())
            sb.append(", ")
            sb.append(reader.readInt())
            sb.append("} ")
        }
    } else {
        sb.append("Первые 20 пар: ")

        for (i in 0..<20) {
            sb.append('{')
            sb.append(reader.readInt().toUInt())
            sb.append(", ")
            sb.append(reader.readInt().toUInt())
            sb.append("} ")
        }
        sb.append("\n\n")
        sb.append("Последние 20 пар: ")
        reader.skipNBytes((b.size-20*4*2*2).toLong())
        for (i in 0..<20) {
            sb.append('{')
            sb.append(reader.readInt().toUInt())
            sb.append(", ")
            sb.append(reader.readInt().toUInt())
            sb.append("} ")
        }
    }

    bi.close()
    reader.close()

    return sb.toString()
}

fun byteListToString(b:ByteArray): String{
    val sb = StringBuilder()

    if (b.size<=20){
        sb.append("Длина файла меньше либо равна 20 байтам: ")
        for (i in b) {
            sb.append(i.toUByte().toString())
            sb.append(' ')
        }
    } else {
        sb.append("Первые 20 байт: ")

        for (i in 0..<20) {
            sb.append(b[i].toUByte().toString())
            sb.append(' ')
        }
        sb.append("\n\n")
        sb.append("Последние 20 байт: ")

        for (i in b.size-20..<b.size) {
            sb.append(b[i].toUByte().toString())
            sb.append(' ')
        }
    }

    return sb.toString()
}



@Composable
fun InputNumber(number: MutableState<Int>,
                labelText: String,
                modifier:  Modifier = Modifier.padding(15.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .height(50.dp)

) {
    var strNumber by remember{mutableStateOf("")}
    TextField(
        singleLine = true,
        value = strNumber,
        onValueChange = {
            if (it == ""){
                strNumber = ""
                number.value = 0
            } else {
                val temp = it.toIntOrNull()
                if (temp!=null){
                    number.value = temp
                    strNumber = it
                }
            }
        },
        label = {Text(labelText, fontSize=12.sp) },
        textStyle = TextStyle(fontSize=14.sp),
        modifier = modifier
    )
}

@Composable
fun InputString(str: MutableState<String>,
                labelText: String,
                modifier:  Modifier = Modifier.padding(15.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .height(200.dp)
                    .fillMaxWidth()
                    .verticalScroll(ScrollState(0))

) {
    TextField(
        value = str.value,
        onValueChange = {
            str.value = it
        },
        label = {Text(labelText, fontSize=10.sp) },
        textStyle = TextStyle(fontSize=14.sp),
        modifier = modifier
    )
}



@Composable
fun KeyInput(): Boolean{
    var flag = true
    Column (modifier = Modifier.padding(vertical = 10.dp)){
        flag = InputP()
        if (flag){
            InputG()
        }
        flag = InputX() && flag
        flag = InputK() && flag

        if (flag){
            y.value = power(g.value, x.value, p.value)
            Row (modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically){
                Text("Y value: ")
                TextField(
                    value = y.value.toString(),
                    onValueChange = {},
                    textStyle = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState(0)).height(50.dp).weight(1f),
                    readOnly = true
                )
            }
        }
    }
    return flag
}


fun getG(p: Int): IntArray {
    if (p <= 1) return intArrayOf()
    if (p == 2) return intArrayOf(1) // Особый случай для p=2

    val pm1 = p - 1
    val factors = factorize(pm1).distinct()
    if (factors.isEmpty()) return intArrayOf() // Нет делителей (p=1?)

    val primitiveRoots = mutableListOf<Int>()

    for (g in 2 until p) {
        if (isPrimitiveRoot(g, p, factors)) {
            primitiveRoots.add(g)
        }
    }
    return primitiveRoots.toIntArray()
}

private fun isPrimitiveRoot(g: Int, p: Int, factors: List<Int>): Boolean {

    for (qi in factors) {
        val exponent = (p-1) / qi
        val value = power(g, exponent, p)
        if (value==1) return false
    }

    return true
}

private fun factorize(n: Int): List<Int> {
    if (n == 1) return emptyList()
    var num = n
    val factors = mutableSetOf<Int>()

    while (num % 2 == 0) {
        factors.add(2)
        num /= 2
    }

    var i = 3
    while (i <= sqrt(num.toDouble()) + 1) {
        while (num % i == 0) {
            factors.add(i)
            num /= i
        }
        i += 2
    }

    if (num > 2) factors.add(num)
    return factors.toList()
}

@Composable
fun InputG(){
    var curEl = remember { mutableStateOf(0) }
    Row(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically){
        Text("Choose g: ")
        dropDownMenu(mod = Modifier.weight(1f), curEl = curEl)
    }

}

fun power(x: Int, n: Int, mod: Int = Integer.MAX_VALUE): Int{
    var a = x
    var z = n
    val modulus = mod
    var result = 1

    while (z > 0) {
        while (z % 2 == 0) {
            z /= 2
            a = (a * a) % modulus
        }

        z--
        result = (result * a) % modulus
    }

    return result
}

@Composable
fun dropDownMenu(mod: Modifier, curEl: MutableState<Int>) {

    var expanded by remember { mutableStateOf(false) }
    val suggestions = getG(p.value)
    var selectedText by remember { mutableStateOf("") }
    selectedText = suggestions[curEl.value].toString()
    g.value = suggestions[curEl.value]

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Row (modifier = Modifier.padding(5.dp)){
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 12.sp, textAlign = TextAlign.Center),
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier.defaultMinSize(8.dp).height(44.dp)
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded }.size(18.dp))
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(width = with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            suggestions.forEachIndexed { ind, label ->
                DropdownMenuItem(onClick = {
                    selectedText = label.toString()
                    curEl.value = ind
                    expanded = false
                    g.value = label
                }) {
                    Text(text = label.toString(), fontSize = 12.sp)
                }
            }
        }
    }

}

@Composable
fun InputP(): Boolean{
    var isCorrect by remember { mutableStateOf(false) }
    Row() {
        InputNumber(p, "Input p", Modifier.padding(start = 10.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .height(50.dp).weight(1f))

        isCorrect = isSimple(p.value)
        var textColor = Color.Black
        var text = remember { mutableStateOf("") }
        if (isCorrect) {
            if (p.value > 255) {
                text.value = "The value is correct"
                textColor = Color.Green
            } else {
                text.value = "P is simple but less than 256. It can lead to collisions"
                textColor = Color.Yellow
            }

        } else {
            text.value = "P must be a simple number bigger than 255"
            textColor = Color.Red
        }
        TextField(
            value = text.value,
            onValueChange = {},
            textStyle = TextStyle(fontSize = 14.sp, color = textColor, textAlign = TextAlign.Center),
            modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .verticalScroll(rememberScrollState(0)).height(50.dp).weight(1f),
            readOnly = true
        )
    }

    return isCorrect && p.value>255
}

@Composable
fun InputK(): Boolean{
    var isCorrect by remember { mutableStateOf(false) }
    Row() {
        InputNumber(k, "Input k - session key", Modifier.padding(start = 10.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .defaultMinSize(minHeight = 50.dp).weight(1f))

        isCorrect = k.value in (2..<p.value-1) && checkMutualSimplicity(p.value-1, k.value)
        var textColor = Color.Black
        var text = remember { mutableStateOf("") }
        if (isCorrect) {

            text.value = "The value is correct"
            textColor = Color.Green

        } else {
            text.value = "K must be less than p and mutually simple with p-1"
            textColor = Color.Red
        }

        TextField(
            value = text.value,
            onValueChange = {},
            textStyle = TextStyle(fontSize = 14.sp, color = textColor, textAlign = TextAlign.Center),
            modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .verticalScroll(rememberScrollState(0)).weight(1f).defaultMinSize(minHeight = 50.dp),
            readOnly = true
        )
    }

    return isCorrect
}

@Composable
fun InputX(): Boolean{
    var isCorrect by remember { mutableStateOf(false) }

    Row() {
        InputNumber(x, "Input x - private key",Modifier.padding(start = 10.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .height(50.dp).weight(1f))


        isCorrect = x.value in (2..<p.value-1)
        var textColor = Color.Black
        var text = remember { mutableStateOf("") }
        if (isCorrect) {
            text.value = "The value is correct"
            textColor = Color.Green
        } else {
            text.value = "X must be less than p"
            textColor = Color.Red
        }

        TextField(
            value = text.value,
            onValueChange = {},
            textStyle = TextStyle(fontSize = 14.sp, color = textColor, textAlign = TextAlign.Center),
            modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .verticalScroll(rememberScrollState(0)).height(50.dp).weight(1f),
            readOnly = true
        )
    }

    return isCorrect
}

fun checkMutualSimplicity(x: Int, y: Int): Boolean{
    var a = x
    var b = y

    while (a!=0 && b!=0){
        if (a>b){
            a%=b
        } else {
            b%=a
        }
    }

    return a+b==1
}

fun isSimple(x: Int): Boolean{
    if (x<=1) return false
    if (x==2) return true
    if (x%2==0) return false

    var border = sqrt(x.toDouble()).roundToInt()

    var res = true

    var del = 3

    while (res && (del<=border)){
        res = res && (x % del != 0)
        del+=2
    }

    return res
}





@Composable
fun Header(text: String){
    Text(text,
        fontSize=22.sp,
        color= Color(0xffffffff),
        modifier = Modifier.fillMaxWidth().background(mainColor).padding(22.dp)
    )
}


fun main() = application {
    Window(state = WindowState(width = 500.dp, height = 350.dp), onCloseRequest = ::exitApplication) {
        window.minimumSize = Dimension(1000, 700)
        App()
    }
}