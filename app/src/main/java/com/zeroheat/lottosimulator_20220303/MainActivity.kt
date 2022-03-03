package com.zeroheat.lottosimulator_20220303

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

//    내번호 6개
//    코틀린은 단순 배열 초기화  int[] arr = {}; 문법 지원 X

//    숫자 목록을 파라미터로 넣으면 > Array로 만들어주는 함수 실행
   val mMyNumbers = arrayOf(1,8,13,16,40,45)

    //    컴퓨터가 뽑은 당첨번호 6개를 저장할 ArrayList
    val mWinNumberList = ArrayList<Int>()
    var mBonusNum = 0 // 보너스번호는, 매 판마다 새로 뽑아야함. 변경소지 O, 화면이 어딘지는 줄 필요 X. 바로 대입 var

    //    당첨번호를 보여줄 6개의 텍스트뷰를 담아둘 ArrayList
    val mWinNumTextViewList = ArrayList<TextView>()

//    사용한 금액, 당첨된 금액 합산 변수 (바뀌는 변수라 var씀)
    var mUsedMoney = 0
    var mEarnMoney = 0L // 30억 이상의 당첨 대비. Long 타입으로 설정.

//    각 등수별 횟수 카운팅 변수
    var rankCount1 = 0
    var rankCount2 = 0
    var rankCount3 = 0
    var rankCount4 = 0
    var rankCount5 = 0
    var rankCountFail = 0
    
//    현재 자동 구매가 진행중인지 구별하는 변수
    var isAutoNow = false

//    Handler로 쓰레드에 할일 할당 (postDelayed - 일정 시간 지난 뒤에 할일을 할당)
    lateinit var mHandler: Handler

//    핸들러가 반복 실행할 코드를, 인터페이스를 이용해서 변수로 저장.
    val buyLottoRunnable = object : Runnable{
    override fun run() {
//        물려받은 추상메쏘드 구현
//        할 일이 어떤건지 적는 함수

//        쓴 돈이 1천만원이 안된다면 추가 구매
        if(mUsedMoney <= 10000000){
            buyLotto()

//            핸들러에게 다음 할일로, 이코드를 다시 등록
            mHandler.post(this)
        }
//        그렇지 않다면, 할일 정지
        else{
            Toast.makeText(this@MainActivity, "자동 구매가 완료됬습니다.", Toast.LENGTH_SHORT).show()
        }
    }


}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setValues()
    }

    private fun setupEvents(){


        btnBuyRandom.setOnClickListener {
//            처음 눌리면 > 반복 구매 시작 > 1천만원
//            1회 로또 구매 명령 > 완료 되면 다시 1회 로또 구매 > ... 연속 클릭을 자동으로 하는 느낌

            if(!isAutoNow){
                //            핸들러에게, 할일로 처음 등록(할일 시작)
                mHandler.post(buyLottoRunnable)               
                
//                자동으로 돌고 있다는 표식.
            isAutoNow = true
                btnBuyRandom.text = "자동 구매 중단하기"
            }
            
//            반복 구매중에 눌리면 > 반복 종료
            else{
                
//                핸들러에게 등록된 다음 할 일(구매) 제거
            mHandler.removeCallbacks(buyLottoRunnable)
                
                isAutoNow = false
                btnBuyRandom.text = "자동 구매 재개하기"
            
            }
        }


        btnBuyLotto.setOnClickListener {

            buyLotto()
        }

    }

    private fun buyLotto() {

//        사용한 금액 늘려주기
        mUsedMoney += 1000




//        6개의 당첨번호 생성
//        코틀린의 for문은, for-each 문법으로 기반.

//        ArrayList는 목록을 계속 누적 가능.
//        당첨번호 뽑기 전에, 기존의 당첨번호는 전부 삭제하고 다시 뽑자.

        mWinNumberList.clear()

        for ( i  in 0 until 6  ) {

//            괜찮은 번호가 나올때 까지 무한 반복
            while (true) {

//                1 ~ 45의 랜덤 숫자
//                Math.random()은 0~1 => 1 ~ 45.xxx 로 가공 => Int로 캐스팅
                val randomNum =  (Math.random() * 45 + 1).toInt()
//                중복 검사 통과 시 while 깨자
                if ( !mWinNumberList.contains(randomNum) ) {
//                    당첨번호로, 뽑은 랜덤 숫자 등록
                    mWinNumberList.add( randomNum )
                    break
                }

            }

        }

//        만들어진 당첨번호 6개를 -> 작은수 ~ 큰 수로 정리해서 -> 텍스트뷰에 표현

        mWinNumberList.sort() // 자바로 직접 짜던 로직을 > 객체지향의 특성, 만들어져있는 기능 활용으로 대체.

        Log.d("당첨번호목록", mWinNumberList.toString())

//        for > 돌면서, 당첨번호도 / 몇번째 바퀴인지도 필요 => 텍스트뷰를 찾아내야함.

        mWinNumberList.forEachIndexed { index, winNum ->

//            순서에 맞는 텍스트뷰 추출 => 문구로 당첨번호 설정

            mWinNumTextViewList[index].text = winNum.toString()

        }

//        보너스번호 생성 -> 1~45 하나, 당첨번호와 겹치지 않게.

        while (true) {
            val randomNum = (Math.random() * 45 + 1).toInt()

            if (!mWinNumberList.contains(randomNum)) {
//                겹치지 않는 숫자 뽑음.

                mBonusNum = randomNum
                break
            }

        }

//        텍스트뷰에 배치

        txtBonusNum.text = mBonusNum.toString()
        //        내 숫자 6개와 비교, 등수 판정
        checkLottoRank()
    }




    private fun checkLottoRank() {


//        내 번호 목록 / 당첨 번호 목록중, 같은 숫자가 몇개?
        var correctCount = 0

//        내 번호를 하나씩 조회
        for (myNum in mMyNumbers) {

//            당첨번호를 맞췄는가? = > 당첨 번호 목록에 내 번호가 들어있나?
            if (mWinNumberList.contains(myNum)) {
                correctCount++
            }
        }

        //        맞춘 갯수에 따른 등수 판정
        when (correctCount) {
            6 -> {
//                30억을 번 금액으로 추가
                mEarnMoney += 3000000000
                rankCount1 ++
            }

            5 -> {
//                보너스 번호를 맞췄는지? => 보너스번호가 내 번호 목록에 들어있나?

                if (mMyNumbers.contains(mBonusNum)) {
                    mEarnMoney += 50000000
                    rankCount2 ++
                   } else {
                    mEarnMoney += 2000000
                    rankCount3 ++
                   }

            }
            4 -> {
                mEarnMoney += 50000
                rankCount4 ++
            }
            3 -> {
//                5등 -> 5천원을 사용한 돈을 줄여주자.
                mUsedMoney -= 5000
                rankCount5 ++
            }
            else -> {
                rankCountFail ++
            }
        }

//        사용 금액 / 당첨 금액을 텍스트뷰에 각각 반영

        txtUsedMoney.text = "${NumberFormat.getInstance().format(mUsedMoney)} 원"
        txtEarnMoney.text = "${NumberFormat.getInstance().format(mEarnMoney)} 원"

//        등수별 횟수도 텍스트뷰에 반영

        txtRankCount1.text = "${rankCount1}회"
        txtRankCount2.text = "${rankCount2}회"
        txtRankCount3.text = "${rankCount3}회"
        txtRankCount4.text = "${rankCount4}회"
        txtRankCount5.text = "${rankCount5}회"
        txtRankCountFail.text = "${rankCountFail}회"



    }




    private fun setValues() {


//        반복을 담당할 핸들러를 생성
        mHandler = Handler(Looper.getMainLooper())

        mWinNumTextViewList.add(txtWinNum01)
        mWinNumTextViewList.add(txtWinNum02)
        mWinNumTextViewList.add(txtWinNum03)
        mWinNumTextViewList.add(txtWinNum04)
        mWinNumTextViewList.add(txtWinNum05)
        mWinNumTextViewList.add(txtWinNum06)

    }

}