package at.forsyte.apalache.io.annotations

import at.forsyte.apalache.io.annotations.AnnotationParser.{Failure, Success}
import org.junit.runner.RunWith
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{alphaNumStr, alphaStr, identifier, listOf, oneOf}
import org.scalacheck.Prop.{AnyOperators, falsified, forAll, passed}
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class TestAnnotationParser extends FunSuite with Checkers {
  // generators for the case classes
  private val genStr = for {
    // I would like to check a string that contains ASCII characters, but not the quotes (").
    // Unfortunately, suchThat is too imprecise for that as scalacheck is giving up too quickly.
    // Hence, check only alpha-numeric strings.
    text <- alphaNumStr
  } yield AnnotationStr(text)

  private val genInt = for {
    i <- arbitrary[Int]
  } yield AnnotationInt(i)

  private val genBool = for {
    b <- arbitrary[Boolean]
  } yield AnnotationBool(b)

  test("test on empty input") {
    AnnotationParser.parse("") match {
      case AnnotationParser.Success(List()) =>
        ()

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  test("test on one-line input") {
    val expected =
      Annotation(
          "greet",
          AnnotationStr("hello"),
          AnnotationInt(2021),
          AnnotationBool(true)
      )
    AnnotationParser.parse("""  @greet("hello", 2021, true)   """) match {
      case AnnotationParser.Success(List(parsed)) =>
        assert(expected == parsed)

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  test("test on one-line input with arbitrary text around") {
    val expected =
      Annotation(
          "greet",
          AnnotationStr("hello"),
          AnnotationInt(2021),
          AnnotationBool(true)
      )
    AnnotationParser.parse("""\* zxfzx @ hjhsd99. @greet("hello", 2021, true)  zzz vvv!#@ """) match {
      case AnnotationParser.Success(List(parsed)) =>
        assert(expected == parsed)

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  test("test the special form of a one-argument annotation") {
    val expected =
      Annotation(
          "type",
          AnnotationStr(" (Int, Int) -> Set(Int) ")
      )
    AnnotationParser.parse("""  @type: (Int, Int) -> Set(Int) ;""") match {
      case AnnotationParser.Success(List(parsed)) =>
        assert(expected.toPrettyString == parsed.toPrettyString)

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  test("test on multiline input") {
    val expected =
      Annotation(
          "greet",
          AnnotationStr("hello"),
          AnnotationInt(2021),
          AnnotationBool(true)
      )
    val text =
      """  @greet("hello",
        |         2021,
        |         true)     """.stripMargin
    AnnotationParser.parse(text) match {
      case AnnotationParser.Success(List(parsed)) =>
        assert(expected == parsed)

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  test("regression") {
    val expected =
      Annotation("type", AnnotationStr(" Str"))
    val text =
      """  \* TODO: use a model type here
        |  \* when #570 is closed: https://github.com/informalsystems/apalache/issues/570
        |  \* @type: Str;""".stripMargin
    AnnotationParser.parse(text) match {
      case AnnotationParser.Success(List(parsed)) =>
        assert(expected == parsed)

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  test("multiple annotations as in unit tests") {
    val expected =
      List(Annotation("require", AnnotationIdent("ConstInit")), Annotation("require", AnnotationIdent("Init")),
          Annotation("ensure", AnnotationIdent("AssertWinner")), Annotation("testAction", AnnotationIdent("Next")))
    val text =
      """@require(ConstInit)
        |@require(Init)
        |@ensure(AssertWinner)
        |@testAction(Next)
        """.stripMargin
    AnnotationParser.parse(text) match {
      case AnnotationParser.Success(parsed) =>
        assert(expected == parsed)

      case r =>
        fail("Unexpected parser outcome: " + r)
    }
  }

  // For some reason, if there is a bug in the parser, e.g., comment out boolArg in TlaAnnotationParser.arg),
  // then the shrinker produces a useless empty test.
  // Disable the shrinker.

  import org.scalacheck.Shrink.shrinkAny

  test("parse OK on random good inputs @foo(arg1, ..., argN)") {
    check(
        {
          forAll(identifier) { name =>
            forAll(listOf(oneOf(genStr, genInt, genBool))) { args =>
              val annotation = Annotation(name, args: _*)
              AnnotationParser.parse(annotation.toString) match {
                case AnnotationParser.Success(List(parsed)) =>
                  annotation ?= parsed

                case AnnotationParser.Failure(_) =>
                  falsified
              }
            }
          }
        },
        minSuccessful(200)
    )
  }

  test("parse error on random bad inputs") {
    check(
        {
          forAll(alphaStr) { str =>
            AnnotationParser.parse(str) match {
              // Pass the test on successful parse.
              // To see how testing is different from verification,
              // replace 'passed' with 'falsified' and observe that no error will be found ;-)
              case Success(_) => passed

              case Failure(_) => passed
            }
          // no exceptions
          }
        },
        minSuccessful(300)
    )
  }
}
