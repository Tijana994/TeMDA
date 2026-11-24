# TeMDA

[![Tests](https://github.com/Tijana994/TeMDA/actions/workflows/tests.yml/badge.svg)](https://github.com/Tijana994/TeMDA/actions/workflows/tests.yml) ![Coverage](.github/badges/jacoco.svg) ![Branches](.github/badges/branches.svg) [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**TeMDA** (*Themis*, the Greek goddess of justice + *MDA*, Model Driven Approach) is a framework for
automatic validation of GDPR compliance in Java applications.

TeMDA provides a domain-specific language based on an aspect-oriented approach, implemented as Java
annotations. Developers annotate their business logic, and at runtime the TeMDA framework builds a
model of the personal-data processing performed by the application. The model is validated against
GDPR rules, and an error message is reported whenever the business process is found to be
non-compliant. This way discrepancies are identified and addressed promptly, keeping the software
aligned with legal requirements throughout its operational lifecycle.

## Publications

If you use TeMDA in your research, please cite:

- T. Rajačić, M. Zarić, G. Sladić, G. Milosavljević,
  **TeMDA – A model-driven framework for automatic validation of GDPR compliance**,
  *Journal of Computer Languages*, vol. 87, 101387, 2026.
  [doi:10.1016/j.cola.2026.101387](https://doi.org/10.1016/j.cola.2026.101387)

- T. Rajačić, D. Boberić-Krstićev, D. Tešendić, G. Milosavljević,
  **Validation of GDPR Compliance in a Library Management System: A BISIS and TeMDA Case Study**,
  *Information Technology and Libraries*, vol. 44, no. 4, pp. 1–20, 2025.
  [doi:10.5860/ital.v44i4.17407](https://doi.org/10.5860/ital.v44i4.17407)
  · case study listings: [Mendeley Data, doi:10.17632/5ncspt2rg9.1](https://doi.org/10.17632/5ncspt2rg9.1)

<details>
<summary>BibTeX</summary>

```bibtex
@article{rajacic2026temda,
  title   = {TeMDA -- A model-driven framework for automatic validation of GDPR compliance},
  author  = {Raja{\v{c}}i{\'c}, Tijana and Zari{\'c}, Miroslav and Sladi{\'c}, Goran and Milosavljevi{\'c}, Gordana},
  journal = {Journal of Computer Languages},
  volume  = {87},
  pages   = {101387},
  year    = {2026},
  doi     = {10.1016/j.cola.2026.101387}
}

@article{rajacic2025bisis,
  title   = {Validation of GDPR Compliance in a Library Management System: A BISIS and TeMDA Case Study},
  author  = {Raja{\v{c}}i{\'c}, Tijana and Boberi{\'c}-Krsti{\'c}ev, Danijela and Te{\v{s}}endi{\'c}, Danijela and Milosavljevi{\'c}, Gordana},
  journal = {Information Technology and Libraries},
  volume  = {44},
  number  = {4},
  pages   = {1--20},
  year    = {2025},
  doi     = {10.5860/ital.v44i4.17407}
}
```

</details>

## How it works

1. **Annotate** the application: classes that represent principals, personal data, consents,
   documents, locations etc. get TeMDA annotations (`@PrincipalAnnotation`, `@PrivacyDataAnnotation`,
   `@PaperAnnotation`, ...), and methods that process personal data get creator annotations
   (`@CreatePolicyStatementAnnotation`, `@CreateConsentAnnotation`, `@CreateComplaintBasedOnDataAnnotation`, ...).
2. **Run** the application: AspectJ aspects intercept the annotated methods and add the corresponding
   objects (policy statements, consents, complaints, ...) to the privacy model.
3. **Validate**: the model is checked against GDPR rules written in OCL, and every violation is reported.

The meta-model and the GDPR rules are defined in the
[PrivacyDSLv3](https://github.com/Tijana994/PrivacyDSLv3) project: the meta-model is based on Ecore
and developed with the Eclipse Modeling Framework (EMF), and the rules are written in the Object
Constraint Language (OCL).

## Video tutorials

### Introduction to TeMDA

[![Introduction to TeMDA](https://img.youtube.com/vi/rE7cVhbUNkM/0.jpg)](https://www.youtube.com/watch?v=rE7cVhbUNkM)

## Project structure

| Path | Contents |
|---|---|
| `TeMDA/src/com/security/model/validation/annotations` | TeMDA annotations (the DSL) |
| `TeMDA/src/com/security/model/validation/aspects` | AspectJ aspects that build the privacy model |
| `TeMDA/src/com/security/model/validation/creators` | Creators of policy statement parts (who, what, why, when, where, how) |
| `TeMDA/src/com/security/model/validation/helpers` | Reading annotated objects and finding model elements |
| `TeMDA/src/test` | JUnit tests |
| `TeMDA/lib`, `TeMDA/externallibs` | AspectJ, EMF, OCL and test libraries |
| `ci/run-tests.sh` | Builds and tests the project without Eclipse (used by GitHub Actions) |

## Requirements

- Java SE Development Kit 21
- Eclipse IDE with the Eclipse Modeling Tools (EMF, OCL) and AspectJ support
- The [PrivacyDSLv3](https://github.com/Tijana994/PrivacyDSLv3) project (`PrivacyModel` plug-in) in the same workspace

## Running the tests

**In Eclipse:** right-click `TeMDA/src/test` → *Run As* → *JUnit Test*.

**From the command line** (e.g. Git Bash), with PrivacyDSLv3 checked out next to TeMDA:

```bash
bash ci/run-tests.sh ../PrivacyDSLv3
```

The tests also run on every push with GitHub Actions; the coverage report of each run can be
downloaded as the *coverage-report* artifact.

## License

[MIT](LICENSE)
