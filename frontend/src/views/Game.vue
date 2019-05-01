<style>
@font-face {
  font-family: "Almendra SC";
  src: local("Almendra SC Regular"), local("AlmendraSC-Regular"), url("data:font/woff2;base64,d09GMgABAAAAACyAAA0AAAAAc4wAACwrAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGhYGYACBHBEICoHQKIGbSAuDJgABNgIkA4ZIBCAFhCoHg0sMBxufVkUHctg4MMBevI8oysUoAv6vl9tmzWvaPQV1IwkEIcFlBhNsEgRD++djF2HrA9/rB8zFAhuyS4qqjtJ8wdU6X7VHaOkj3iHYZkeFNjqxMACDEsVGoRVBJAwkLBQztzmnM2PTub1LXYW6qt9+Ljo+auv/vcz5Wg8IIRNN24ll4v55jecfDn/nvT+aS7+3waNFnPj2+Au0SKMNEtaFKC62u/fTizVRxGFmVUSBSXPzfTpzNumz9/K1O6sCuK2KLfcnkEt7L9hw57u+uwPWO8sE5yl5pJcZSJ2J/9SwcHuWhLDsgItwqVfArdyU6yUBAhQKvD8A5t82q/0zSd5jxSQr5dnrRby9bsurhs/CMPMZLAqskhPCKrAaeTeZDBoiaFZVyZlrsqdaiTbFlrmyOy+1KHvTMWQJXPqre2VuvUx3hNYxYWeqz2pD1Kw/o2vYkUCCA4lLGwMQAOAx0DgUBMTVNUPruQYhAOhrdwOgVzaDvGHzNZhNDunDCss/lVWAB+m2DbY1CABWKt8Mh1QBJ8omlsQvTiIMAI4NQeQycJ0aD2zRvFpbxDY/IEGl6dN9rx9MHElEkgfJmxRJ2vbjR02atXZwItR1HEjzSG54xILyvif2+A5s2f+xqnffmjFj2nElsRb5A+H1IPnB4NdUkOkAJwLSU+D/KAIGnZfmIShtchg4KIgrqIxgOgZloCfyaRXlEcm7DtLLB7h7w8CsZE/Ty2Hg42VgtPfw/KjFAQqSOskoGiKjEKHDE4LWhKBW1pOGRUIONUkUVy/3G0paf5HlYUKcRvpepExEbYXM/7QBFqKg1yl5w3w5pVWe2bAG0AHvcIAuUQx5tJ1/bOVT/11cf7eetvfzF+66o6QkEVQdc6kwLs1+a2Oz8+JhQ7qS0/2WG7ZXOHWtMpXjTCWYOLoQFcvMFsBqRAZoAVEaWk3Va3A2IEYsSuBJ0Co2xeMAB8iAKJkP413NcHSqwzxCu2dmW03IBZ1SHBTvkrqi94WoidFdt9dq1VHVucS1WKKlCrOq2URlsy1zKdmQVjelQKd0ZTocolM9x2g9NAOjE27GGyOTMEOSNKKhowcXLlmlK6h80Sme310+y83iUjHg5rT2tvBcvKnyMlp3BN5cgIWRsBHXuFMjQEtkN95UPKGIbvuo9OrHzgRZqnfismNPdPDt4JIHaWS2x5NLi4CQLhHYQEtYYFzFJgmf78YXLlzxaeCdvb+L+FjkuOSrnLO1nZe9sBHRq7IaoyYLrMu+EEwFWn+iudSXp6jqqQkiY2c/ODUycoFtKoCkJsd2pcOa3dHszjBlUG0aShmvaMz9ciomPEgHaQCZsCXfWB5/bfnmmMY/T4BI33pPjr7uIa0NGji5hH3cu7/KLI6DSW6TZNG2GGT9N01u2gWraE+BAouTigVPPS1XWPEJq9Tloml8ZzUs7TdqDNFUCTnc5DtvQQZR2SmhY0aZx2jVMU88pVY6sUNOcZj4aWRAtERuNzK2AKDNM3YHJmIdaunosciaxWc5xnvy0IJ1XN9TsLFM46zGkwl7xntrjjZRE/n4bU3VEzewQRdDRB39QtmsPWSyqB4LzbPeyNmExpz71QWxCRK2FGSwYsWn9cC/aiBzC8S2asnSfoPqPi6B8OwyJuEpyVNVgyqIMCpYLKh0xA/RLkeak07skifBjOhnaoNxFY8I0skno/YEsGZhs08rc9SASGt8NuEJb87H1b6OLIk0jSTGbY3tCRJ2D0YEjFvexYysmxAJe9gho2W0UtyNetfZiMNCDDUUP5nQgKiL2A68KvDLSuF5inYsTzy5YvwEZIZCsTKmdYbLgSrdf6FxC507TiTfRfzWtBmSJQt5qlhyAlVJHNBMMmWcNgiEVk8RrV3hmD1neY775VUNq2DJbY1XWHvAkI2ln2MfQ/9+1oaFt9IZNn6AVkn5eI5e8QORI7qBoQekbl5echyupQUga2ytjbHHmCfEqHU77TsB0PotvO9rt0sInTKoMctcQdaHGiwdmcxk5+N+nSfo4gZzvxKj/tz2kPKTSZ/G8ywdCyx4qcccWzeY87BT+tVobG+bZ2iA5mFO8rk5+zwR/cA4RhY0Q/HYySmSPTd9iG+8ShVzxah/k288bVC4QEuuV9lBChQMz8oh5s48IgeNW2yTAMT39Zgc3UZpzgZqYXLiztqqx1Tox5odUT0lKXImU8XxDKAa4TJUV7rGgqfaNvmqWIV0ugcXz7K0tIXKMH4quCwQuhzmOw3aNbsjWjc5DjdDwpsRkDWMVsqZ4s4rhuWdxuaEX9CP/v5rRHr4/NBgxTxtbzIueSJ8pld4kmMVnNYmr4NmyCox63hOjPZf7HxPdI7j8KtZQHyAwhd4yFRmAcPyNF3m1HmfilcXg3I9v+04U91Fcg4ytUWp4MGm8cHBeLZNsxv7VVAJrRSsgUlHIrnvVLP0GHLmFJ9TIW8s0lGg6JsBet0Ty1HVoLssTwByIoY+UnPCr6nqmYBauBmtjD/XHZ+mSu8NkJFz3PFiynNgXPrOV3IUH9HYDhh6K2pXcgWycyFy0raboQJkARlhGAq4JJoSRQtaJyJH1x3KP5KbgnbzTfGWzvCY2Kj/K0BvoskvRukphFrRsskB8ZiiGvOBKG9KcAqcNhyeKCoUJqDBWc7URS2DBg2oHL7Yyotcd5zotL3unVli56xlEM4JbcmNSbkP7niQdl0IkSgpI3URKtj/nuSJqyn7HIrj7XIql9xJ+CR/Ro/5FfTJeu2Pto8uprwk2rzQSGfLuuQJ+9xQu8lqGXTXwYhXjbVe+Ya/8jBP4JI+MMohnOsemu1Kmd8/2Lo3pEN2r1iPhdQBvLRUrek91i/f8EdoHIYKYVqsnFOABJjxLrwrpb+7AmOX2sBuWNaxnhK9aIZqs7woDLlOAxhMmmOA2NaZZMMNSikVylVLh2rvzTyJ0xGADFC34mWv4XzBVUOsF+l/Bs6cyU0ger815wM2BHerYNIxpT8o/UKO99niH741Dxk0dN+35wODjwzENe6TvuvLfRm743soV+b2Cbk+FJSncwsERItZfXtolqXXYArLOQRYCLSzPYP5HKY/Qw7z9VNfbB28a1S8AIsZC2ahz1Mtg+JOueh8DaYtFFwwzF/I4Ax9Fjm+sOwFtq6X9RyWE/WBEgFuAt0ngZb3uGteShbsjyh78/ix3cPAIk38dndotUcZxGpn49HJLuKSBw8O841Boyc9658JF6w837vIg/5x6ogc8Zs6fXhTxBJJYZm8xlIhaS9ESuEQZA+P+u3ghks2GybFammzGdG/7L5OvFqKovcitTRqw5SO4G+Ims/KTqWthxTzPPtxdK2ji+p84yHDlCuGqQosz6VwV6fOzttDnwnXQN4FlIBQe5MA065Yk+FsRgXwOrt2J4T2TFBHkUYPnegdL1fIJFCpClFgIXfjiLeX7FK1UgUWIGva3TKPASLb5fjFXOYpTVUOc5AlwYJyr9FHbINBOLB6P2NtGZtMhC95CrJ18as7s8JgupKu+wNxSEtSoASNbMFgsUbz+0HqCypsxcVHJ12z7A4gWc+0n+0ouPHXbiN25i8wy4lNPzatxNKVhZCujMp9ONyc6YAH84Cox+f1Cx54LMWpkeEJuJTQC7cbyq1azPJ4mpwl9Fi0eSb8qpveYugGa7YaJ2J123c9qtGjd6JnrCJZKDtw5jOlWB1AWV5hvVL6fkx3kWjpVNBQiA5kJDHSdBWRnIP4e1eiw5zrzrfI4REjgKMxaqWg0mdcuag83xhrbhuwTZ647cxzbgkpyJYvu7oqiyBLT4TiVRopxaNcLicMaSoiCLUQMnbtWohIMOEB2UkEGH8pZvk1F/0zKOy070niYSqEQjSk0eQrUWGejUPYUF1UG/tfjjt5/kLsuEgzBdm2+LTOnJfZjS6yN71LeLFwhsHSMp1NpB+pB2gRVngSlZz4QnzhquMDwVQseyY5Q3BgpNECc4spqUeNLCnA9VpKL7bZ5dKMVQRd8s1k/Or3q3nctm3vMiDPys1WIR3c8djTHjwWab1wxaAvPNpSTZn+fcylYsmSQZea3o0BtXvEAQLkrCERPuN6QCfGbeAR+9/NjyyqW3xpiUMYijSqOnjJIcoqCTUXKUspvnVmMdyiSm0oU+AMwheiAqoX2lkRSa8TnOepshFpqrqqOmU3vZr1EswiLLMHXGLSXLHg6J939pnkW+1rGXiTt4dq1kQH3G80mDsjxvQ+OSr+9yaRWw01y7Za7ePVaEyQzqIJznjoqAHdROONhlpHtxbLTM5kfVcFhsYx/0Q//BhaUtuYE9/qeYvqiJczwdLVfY3nSdqPgQazUJmuLUEnsvVWhdj7v0W4Rtk5RCMlmBNTu8NDbym67/Go9+qXWbJJXuKpVr5sZvYUVNpsmdcqqOJculDbkvcoUFiYdRn+8FvyLLHPc6dKX37CwhELgQgwVXYTkkyINHvCaC1ZXJMUT9he5KLOL6vdm+BVD2kDXYpZFSK+dcZFOktpl3M9s7NsJFY0uV5FINrcEFh31UDUCrVRK+oUm2mDph5z6zBZYRXgFsop6zKCBsqbCRFOrLb5XVC547IN1agzlTVoV0UfsRV0wuUdZnLHt7KyKjmzRsQFppeBcZCQPhBecLKOXwMetSsEWA1IwkURdLU6J6Jdxp74BSJgpmg9Zh/39middn4qvLaJ/T7D4Idlvt0zVo0zS37mOefBASJ/0Gkyy4gD5DJP1DCQUCCCsG4TPhAjQemQM/qL9GWJrP2zF9HeX4oHhBT1SPDoP6b0jyR/vslTsQfrGMuSS/HXpsMnodhnbHRu6UMPqmRYLTvXS3E9IGtOcnniUq2PGsyVrB+s88QGa9JLzPXV12n1TbyZ88I8USicGDPMSE9nrU8mkC7OWq876VsbH7y8YWNp8tz03fNYBXKaPJuR0+E1OJ7dXe/meEgWiXl1gYwWg1QP3PjpgM6srBywzu4fswKscLcBPcAKbhv0Ie1ki4MrcG3QkV+Y2kDEFuc3e5elvRwGHlyptwZb7cs+9mEJcczDwrGPN+dLIsuO3AFp52zcK0v+e/tn456Dl4l3h71TzbofcQw9Yw7YqD5Kw4lIC1JvpULKmg9a25JhyJ6Am3AH296WorrV9+hrGQ7Ig4o+tt1MOMGd8joUccl5gn+Ah5ipIO6wJYqlkkXsOTq08uitYRYPi13NRb6edJpHV02aUQWFINLUGD3w9nIOIxvKLHmbD7ENccJkMe8Y2ra65/MWPBkNRSCLV3Ln152DBIo2agldtpgLkh+ukCSUkPNIxtvNXmQm2ADk08oBm4uXnLQDTf0ZabJjtMXf+DcTFB9Nk2Y4duNtaWwABPMNQDXYlpogHWktAYClHbID7JNshbh4VOlKx3WT7LGFDncCPB2wbB9/JflmMXrziTvke5u+n6T2b1X6oiKobUtTPRzLsBX2ABl+cZPHvjSYZa6I0onqUXnOiBm+l0TSAkOerUDFfOwzkqV6o90Uoalw4ZxVupB7d1CKobP60WtL7lztXeAPFyPd3fakOftXNiYPhAdVYJSvlltOOWqemCpSsmonS8QYLbfcdBCv0gxbVXNlF338SfSMF73NQMU8Ce3Qip8kzp5N/CTbpa6r75FUsuerDXSzgrHBmj0EYjqvzOCwh9K6+2rDnecOggWVAsBC565eBLVu+ZOJMg/WQ4nPEdU2dtn44qKIeB0qvAxaOxKH2G2zuAhaszQOkeVyxaGV7ViGM0MSIC63jo7KcZat03/5D4dNSxEUGJID1RlHoPK/1rPVQP6HtQIhcPKjGFmI4dzIjFW2ISkFAhLQIeDjCqXUxUqB4DtRCW3X0NvzLva+O+KqDUKuypHsCCImMOAtSWiwO8Jpn3trrk610VuYKa9jPUSsPGnu5O2wY0rpiaz6/Bof7/fWGqvUZINOpbWSW6WmGhiJqYEpyVee1s6LBrIa942qSadqvuG2rfr4+gRdnG+tzVZ4mVxj6i33FK3297nZPMqtrBQSivosmeIqvQwuhKULo2Nrc4V2UpH9p3YFX2ldSgyzHiqyypEp86J7MuQt0hJjbyhY2V4EHAreZCSN9xz93CT9KDSMg/6a0ZFDETAqx434fB02fe45Og7OrV5mdwnJC10Nc9BOg0yuANh9HIQPjh/r0we8NgKxNJLNtHqlaB1ga8hI3iW7ZScF/FPL7I6ieOQ1/ZoIHly/nwWrtzoUzdtx6YGAh01o053RJwdrWxoQT86/EcOXyCC8+uzkPrCazhZaHo3HecVhx+7C6OEDpWeaBlOtu2PdZgQAuyjAGUQBjJbjlVYRQLWQq3dPsVeELwf0xpbxJXW9rnUuXS1jBehOnpGxhtfT2srref5OADfuh1kC5XZWZIVd3bRtkJVqd1i7+fQDb7K5zp62Bx0PVqfYZObgl52UbOAV79tK6oLD/14VzkgvV2v4hQytyee3Ir/lyNWpu67gGKWGpvR4TUZS4+yI02tgWUollCc6x4VdpAtkEiGnfZXDq+nINGTbBoklanBTHXHNwBbrnK7+sXlrhYU5an8LgWWVDheBFAe8/DWU8vvhv3uZQd4s5nzZreBW430fj3P5rL+4yVllE+zc8d+TCthl2pqPCQD6G6jUVuQS7QMpoD7Zj9sRFSbkhzfBzIRN0yKFtrVdjNQUhGBbcp1NkLwMg9LfpMmTArY6P+wlmSvixtg8Q33sSc/VaNU1+YlanVFbs+p8RfCJ1JjiKRCBWJYrE34hIuNjegUFsTk+N082hrS3ahXC6U25jgcR3T0BQnOgXrUF1H/+DgZnuMXybLZimaukSD8jzpU0VStNkRpzuXpFZBI7cWIf/QJ2wLgBfUkpZpsW13Jzgs5u8eCl0CSJgCMa1QkVcmM5UJAT2XCBEz/cbS747dDr+Ze5j8v/y8gKaozP64bndcUD7rnN6PvZyX1batEXzuLBXw+5BF+6ige64rvjGwchm0E5+J1J/ld2xP8vV6GhTCVTqGJ0JhMyDZY05NjtUFfntVzr3Tnw71TssVRsRLcEm6fbrFPzfQRTJk96noweJ055WDg6sHYwpq34QLztrs2bdp19SjxONDKXDV6vts3+rQhvJTJN2oqTfFjmJc4Rdzn1G+Jq5f/cNC7bVOFNGwKPTJiROHyGqI8aj9ouDb9IQfGhw5zGKees2k3c5yMbrXeiCWatTUn0CmR/966n+KCaDq9R2xW4qz1iTt3pBe274WXeIQoQMqxFzUFXfptfpkivhcfpBPykZSMpTjEaMkYtci5+q0PtK4X8JumbFyO/EfJdBg9bIPAuUqPMInhqVB01k7viqAichPMoqWnsmw/jKhb6p5Sk0rrOsWcbB43E3Hx7BDGtG6Vy6Q+UN7Wx89btYchArq1z8Tg6U8H5/6RFbbTD9Aw9XegTl0bGqKcqGC/QRjk8Vd+tYhvstUN5MqnVmq0ivDIvpjfnkUKsTpdXGChuVT7yD1AgLS7dgrFEHQoVP77RP1EWPKBLz3kpECRVg/M4ueMR084SVrUrKgc3ADXnTV0MPWgHWHxmoNo5yr/BN5uQFsdqzjphvx2MnmrNCrOJtf4lOjMpcqq9sVDgVF8/iht+scArP98e4ZK/A6Fw6Qx529nMTmm3A/we8yF8vKiP+mnnXPhFCsYMDscjh6fW4CYu0cMeyWktwZZAlOgm+YfFy/BSV6QMMpKAGRWiwTTKQBseCMMz8JveZG6OvvUvuGvBylbgM+RJ14EGysVw6fYsKNQ+cCk76gBKIWifSZw18RK7S8+OoXXKisR9WCWuCFwyhqXoTU3iMMB9XgW1T2PQ2s6W7TUUzQ37CINqLYZMgHrEbhHDCZmcgdDSyhnoGEY8mUcMrQd3LfjUJpTOzqPZupKhOS5Xe8SOZuhL9k6segp5OgJEabGKvz1zsuDz27HqabrDjrC4/xHSBaSKB6rxbaFmEBS2taT+HmQyj/LvPgKupmWA5XHPob+KbDPBfb5sKj7K6ONakPRD+ADAUre6r1rFfN17N1IkttP4rPVRh/Dx5PPh0iKoV/PzLdigTR98ZWaj8yx71YfLuVfeVYqL+0a82FZm2bb6cxCYHl84ZNN5p4FR07ei8cJI2z5J3niJeVt6VGWQftFUjNwlmHikw9HxllP6dlukL6+4LBvUkHGqSkxRcdBciD2ZmeJdQyVZNpmx+ARlwoCAE0+5hkprxqWZ/kMSsAYTPS1RdR7kCwPm2N02+w0hCajJY+bTNX7KODm9XBUQX/auTu1bN7+04V3/5WrS/IVD6Pnnq4Nb3iz2JZxwCfX6sh+0eOMzLCMyrM0riE5RvX7MHirlYvjGrudc4Jdk3NHqRcbHKSQdYnqCTzYqrRcrg+A3+61p8qYA3AbKGbImyGnjoRJ75iJB1IJejlk5NGRWRkka65WuKXJbx1hEjgtHuj5XHCCIFGhi1LxImY+5ZKWJshAvC48CP49O28nJ9ynfQkRYWTZRhUAW5+Azgnm/iOcTnPK1ajyFEf12cYM7vBmOraBc5Mzt/OTbJ8LHH40Kz3zUakvvjorgpXpVpLU0Rlh98vCRVFAzZbiYGe4ZsAG1ceg5dyYJG9+6as/hGqBvwjcFRquNZrZrDoQfl5jcuM+7EPgcFjinpZxd8JJyEVTD/ikU85JSau5kYsKPhAsi5pM3q36PP8g4LhaVSqu+W3wPPef7DDlK8gecUW/Yj56D/8443jVv5Vq0SstndYCTmU4RXIeyeOksRNM5e5LjspWFWfMrDy++5SvqoypR6ztCpkSUaeDEGrw5aB9xFE+/vGQGvP2XuLkoDZzbUcdeHFpSeidMXS9ORm5MR5cPmJ/NE1j5O6PXOhHYozli+0wfHtJe9kHl7ifFnKMUq4LSk2nAk1nB+Lshg6XeVLFJtYxXYHvik2O/B7A6XnzJjBF9x4jMl4qtrAgKiIliUkAIVsCFhtiNpXd6Jn39AncxBSwu5noaqNmPQdiuXv9PQIUzpEv6ibSH8abf0sYuAXjbZvRAdnKtqRbMuznkPNgg0cStPVyMQYMiByP2uAEbcf8DVuWLVbH14Z9G6yKIkzpo3guTr4OKFOTDMi+WRtSPNs/tRYtRuRmEAzV2XnaAjPwdmY36oOfFPYVHx7OrmuoF9vPUDuPbOkT2aWJ0kpvnqQMxu/pZqRleB5FCYlhbzjCEJVBwZGyAjxBfeCjQGj2zSuZfbd5ndovXWyMckDSjS4WvOt+VIsmdEFAvAx/kS2Tl/KLb7Qdy3Ihq+y3Sd91iPx5ZcRDJcw91KnhHlhZkk3igbSIqZ/DLhXKHJ4+yNFBBX2RB1XZlldIRmK6RDFU1SyikqFc+W/gt7UQrZs/5obqpEXfhaGkr04ApCz3oJ8P8xsMKG+Bql0yAjwhcDi/cX+tbgRNXDx1cadUBSyVUfxpep/vKocbNRZaDPFtqc6if7tREP3Nw5SmdX2gzNWI/GVUOyj8Uv5zqvQrVdtKKkZL3R/TptqGhrtkJaFEmm1Jg7nVNIlrQvCtQPX/NWE0LHPMZDC2t1huboNvoteAY0T+3KNrEecfTavZFWPahhYXmJa6j9RHSumreQzdh4ijrObQhYzA3yxqwHMvDPpZh/A6GrhxkLsQo4XcjqN0ov3OzNaq6qjTSVYYLTN4E6GEx3I/kKta8AzaVhnBv1o2ssQjiDcNceLu/FLvAyXaDrRP3NUGWRUwPtNMvkUYJ1n/DGNXo4jklyvcLbaJGYLTQ925XYHlMpE5S/XQfcEUOqtEvs2jFIma7YZ3Bv7tRTOT9Ds+Qa/j5uuVe89td24m2S/ML/XunMcpFzOXLRjv9iWlVTtGWjqQWRXtQ+9z9U5vBelzDwmtN+/JcLEXVd6q3W8i2z+loP3HhfCGPnX2XKj4nXQgCkK+Re5bEL9ap8TwWJuaXamSo1zkkPxrNW3oPF0vHiP3RSTQPMA4va7gksuBdzd9k9EtaL03+tnP3U7JktCSHZbBZeQY8U3JlvUSm60IkrMj4v8peEG5HjhbciL3gwRW3C93csLXF5lzvLUg+r3fWlbcIGbe3mAh+Ck/XUf3KI6ckMZ6csqJ1sqP24/A4F7d4ZpLAy1nC0fhLuJ7RBbWfYks/2rgtROhcfEig4hqJt74mFKt2LOgoykF/RUiwjeuj3VcFE9XbeHKMkxar7nF0c8kEF1yZXRjhzdt+SIt3dBeDI5y37SLd6hfvDMhoR+SY6QaLrcJlnGNom6thynsosFxyGAHIREw1n2Q2A1REvvT+CKwoom70U7iejeSLINQSwVLa+OI21g0jFp4SN8hatNU7Pn1L0FEgQXBqenYsOBKRitYrgMUefeqhPO3VylkatZT6G6SsZiGUi4/nT+m3+m0Ucyo6Xktk1iS3/4ffRrsDlPHiippuJF/0w5WA96hPY3URP1XSFm0sXfbkZ0NE3YZP4VHZzkmtmm53+w2QzOv+0Zu1K5pW/zO6P2LMfINVbcXUj2WsNkMlzkD5ynaqXYrlNVNfWjCyWesthoxuKyFUvX05TZzeXf3dP16W6Mq0bUDxo2Ol3M+9U+tVT4/h+RIR6fVczr4K+3XCXbwLl59V2BUHMdSPK93Ziz47EOweOIKdZ1I0BuIk01eIZ0ttKEzilD5F8x93+z/dTNohv6tyGlxOu+pHO8Ts/mc7FwycdsnL3o386O7Gg/SBBftTNHriFJMibsUW4n3PIzppyPMIRIwhclIFW73G+V4CMjHNJJndVuAH+DOFHnO3PPRFXkySkdwq/ZM1/VaDkoZNjgfHy7xBFuLfzQh5EkxefNC0KtETlvX0JJvvxz1mXRbuRCXobPHFIhfQhitUtV24Cywd8HxIdpuRYZVc1q0miHeyVCH4UybS7nrwdM0pmGrdCBaZOeH9ahqNNpocaQnAv1mM0zueXLAW+WjB9lfypIm1DtkGTKyuX5ukclg7Yc6n8PHqnPQ0hG986zKXKG101u14X0S+ZWGilZzenaBtPfu9PyFZutiAve0e80HhF+I55brkjSEOFyinNTMEH7+BDxALvCJYhuiEeFvnw1XaeWmHgNo9NPs6M9n7wekgKA8nhdzy0eAaYcDq02bUwJJ30/ifv4OlGpe8bLAHccf6TtTfiwbpA87CCJ4qeiBbzucFGJ5l8luRMq/IySdRmRmdmpRVH6IQv693gV9JecHFAwHaOL7jY0AARpZW3TmPYS9cbIAMp2gs3w7vVXglJOgmMnuk82J7AcT8t9K+xPmoqGzPtyfE03PEObbH08QVlz5u2/Fx+/1n50dAntAaexXE8OMn8+B2yf9lvHKxf8qzUrfKYHPyBHdmaJbESCVqiG7KxMt1GqFW+BMoJRZNcA2FWrajKVidRRP1ZvVz0zPkDMecUKGSBo7+1lrbsgn9IDuZB18ifgMc105WVHy6aStQnQZje7qxM13fLtFBfdWCxIDUEJwg4C8nrfdIJQoLp1K9vTCV0Ha6FpvNqsRNnwgr9p3BAvRaHWL+yeq+H9A2sLBHGVmlTv4aKY2qSlA/SNp1K8mWpn+tTD0IwKoZt/AZbFXx1Pi4XVdYj1/8/qkrUh56eCWiLyR9dMA9++ExlKOlWJ7jI5C4FSZ4ViP3HshyIqfJLgoeCxKyK7LtJ4HivKv117iV7fIXDB9trf1kBd1xKBWRwMhtS7arud11TpBVabGvO5UgbZRuxpdlgyW3Bp2HLGjM4eK1cRIN+PmxgygYKanzIPwtscnCinQsz4LsIy5HChx13vu+teONmOM4jY7d5qf17p9KUi63o1gyiUes4vekgTNDXmgX9g8yHpnDptSPy/EeuIr3DXAVJSOQZ06jjetch+uvp7hn2mQNlegmwWpXaPxTI3W3fXWvPV2jIReeaGQzugLuyc/SZoc1QZ9B61chjRFthyNIHVJ3W3thk3RClbEjnFSTnmfb3YoSgUaoMWNFRqfCQaFMXWHokLpZZXmzJ1sis9gkzao+jh6cWFg0kd+PV2eZgh21bEPhAY+sflMTXqjMCXWUM9IzwB1kUdjO63FUTFAFgx53+rNXTTbKduukPPBIVOdqTkEwTTXA7+NZHTDUfP3PFKmKK9U+ZX+yDKxWj+6J2dqHJJZm7eFBtoNRfxGjueb6RPoZAkfIl2/1XILX7YwSH+lTY48rnZ6aU1MODpTygZB70QcV9y7KMFviQlUyncrHPMCaveh7ObhflFWpfGQ6VWjcFvac4Fbfz62RPTsa1keIDI4Hscx96iLRK3GoEu8oCCMu/JeQ50EIVArBH1e++5Xjk3hzH31JyzYf39zlYduMDQ5B5rt0CyYrZmGoY68Yukiq7vylOwZm1KxMiUtQPFCX5HiVJXfUI4HwpaEteEt5sq7DuBy7aUmBudp2RAlcymJeI0PBb9iTrrAmbk7CUpomunRw3ixZD0/7THIIJ1ilUjxQF1SSrZKOZQrGN4V4gESI5WbwfnLSw/06uHJT86dIV5dhlZvKjT/9s13v+NwM+iTf6YU1A7JvZy09HGbMFVDW0T6uEu3RZwBdNZ+ATL9I9Hseh7beFpFz0dufb/pocHlQ0IvKK4Wc/Z/AzkdVjBfolJVlMGiwDDEOXQ4cR0K0kGFQdKNzLVCzVss9enpocl7W9gpx9dQBwQ0X/vRmd91YQX647Lt7M24PrsuIxAVv2aQeHxVX5h7Yi5tY8QwBt7e5CrJ8VF2+MyuvdxRMpCA+r2IWkrN7yt6dHLdZL9gxTmqcOpxXWM6dZSo9vNa7tbAkQ8S7gjm8sMTt/+mfw3U3FhZ/tC5nj2ycK7AgfnNC1/BG6KVJHT8/p4e5EqGfAeIcnGOCL2ITF+pr9v1OqorIZfYhMnM/4V8+1K95Mz61j9gr2Udek7Blk15GlWxjVfzBXL4m5E/6pp2g08GIxTfghA1wpUsmk5ThcAI1RvHmUpih0JZ6KGnqxXWbhalK181nEOhZkYjSrb7CMi/S8xluYu71RuFVXx7HGWmkuE27/QzwEQGXBdSJXIrENV/tW+FipCEdEHrreLNb876rJfOzjJ4C7cMLlvflhnN6SRTUw9ZErK2526ff2dumO66RhGQ6hmRqJMfBw2H4DTlFlFrDo3rRjIp3aq7M97Xbb+M+Wenz18s07HzyykeDi4eC1kU8tr64RQOjJwaoBM0cPtuLw7614vuh3EyDehz+Lyys7uXh/L8LrD68pD+e4iIBd8j7MOmC93mSK+2kg9cF0vP5db28bSUqYfrSd8p7Yj2eoBsZ/cGI6mI47UWNeu56BKjAgZ5fLH4P5unnatohPL7A5jPmRnXK223IjrzOSm+lfi26m8msCZyuwdplOuyuqkqnSOVkqY90gVrXtlHxoUtxfx8tpGfA4+IrCdm25f3ndXvW1WdjfbWSops52adN6Z0q1ilnrGQr7gHYcT+d5bcCXwVshck+ySq3SrVg2y8NeId49KgenQsPEW2fT3jXmlL6lfJVCZJ8gT/3WmcHZjsYXuIU32H0ROuqKQLXA6DUBegP2cm1o7UATn0zzXvIk//n5WO15XIqOptnxK0gKZ7pdfU+hL0HGG5pyC3e22qsujejn6e1PNZb5OgjkUdbf9tTD5H03HbUWurV2j1snzfgSvrsDFjTEZzYIIoZj0rJoOjT6iA5kz4aXKZNxnGhynrH+U7K/Np4gvSYtRLBc/KjTqS9G8NI9iGng2Xhjng8tFuBnNizlT8cAts78AXYnuCUf4bPMFHDam2AY540Ue4XS7G4AS5A+DoVLfjKjvvwa5ganYwLV+NvLA2cPdAKSxfQ3RI/QeALg1cp/x23agBC9i/29AGx7irPXikXsXzF9A1jR73Kuf1c0qAmf69r+KlvRzzf5370/dcDfVszx2OPcsOwAOkLrhghlv43HzFcn3MgAIBY8n9FdWfYRP8CbvADAHB+Qz4CAHBz98KpH+lLnm2DGgCgQQEAAAQ4ih11xQAq+ExLql0zKwFyAeg7+0yIUWKj8PNnYb3hXTo2s2L1xxqJZKsEcjYukpt89eDGDSFms/U+x4a1Yr4YFbPC2JgcM2ExNClH2Or4RhFCc+SrQSW7MQgrNefi5Sf/FlBxfgqC7ZsMRxfQqSxVhaAqY5lqcKaRycy1flaV7hrwY63dvTkVZadX4XomNxvPYKkxTHcMtWVHXkn3fO4rYTIDQQmPkNhZ8h9nAOhO/g6P4jR6rnw9ju5+gf6KXiGaHDT94HsJzTGB3ciPLhOQtQAvCFxDJHzlUwjuzPcEPPwvr3o9Xxw2+eLV/OV+8NpD43c6SeJKxOYZuj+TvwzEwpDsWsp7Ppi8zl4yAP0T4AoUJxCiCHYaQXsAcokeBdtqJBN5jMNyjTjw49tcU/t5iYdE9nA6n+A2MUTc6KESmPP0AbwRrUNZiGAJwDUQAwZDhHWSFM6io1DJcB3YBUKSEAjDR5hz7oJBz1hD/nQJusfLcHhgNMy6nVOu3NeAG+DnC8pwHRKS8AbILT3yn3NoADiqCeRJt1kjUgBwogjkVPeQb58LzU2cVtqXK1UsxSnja0HjRiF8Ili6eYY7kG/UHs3ApQEHRRA7xTME1Wqj5QFTDeeywiUgEsKJS5FQ1l5EwkTydyQcRVokAlF1JJKvxTtrLcTBEyUsK2+oLLDkV5P8cvzXpmxWUJBR2SxTEr80yzSaLEvMyy1W1isvL0NZMYqqUyLPUoLssaCYkVNG+VcaktoIbp3cylrKjFISQ6U9oBJVqCTGkuTNL7YklporPWNvD+yXe8InbNHUUlOc5fk7iMFisaOSE1ITo7LhyQUd+LTginRGKWPTVRWMyoHUpvXtwFUgcvHlkUxmVU7lPFPVdXNG1XxSZTuUhZNMKZFDyen+aAO2/NohjyXHPv57CpAUazFRO3sHR4KT8zwXoqubu4cnycvbx5dMofr5BwTS6AwmK4gdHMIJDQuPiIyK5sbE8vgCoUgskcbFyxLkikRlkkqt0SanpKbp0vUGoykjM2u9Vm2OGPWmXb8eEzab1O3eEiP+/qfPmE4nH/21yhYf/v24znZn53bIzhlkPp975tzlCxcvvc27fuXqTpY/h9y6cTP//W9dCguKSopL1yirKK+sqqmurXtXP79hQePC/dZqWrRYs19/P+j2LrvdeXh32oy99jk1a49fOhx1LOjxz+vmj/5o23X0AQ==") format("woff2");
  font-style: normal;
  font-weight: 400;
  unicode-range: U+0-FF, U+131, U+152-153, U+2BB-2BC, U+2C6, U+2DA, U+2DC, U+2000-206F, U+2074, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD;
}
</style>
<template>
  <v-layout column>
    <v-snackbar :value="getTurn === gamePublicToken && gamePhase === 'Setup'" top :timeout="0">
      <div class="headline pa-2">It is your turn to place an army.</div>
    </v-snackbar>
    <v-flex xs8 d-flex>
      <svg :viewBox="mapResource.viewBox" @click="territoryClicked(-1)">
        <defs>
          <div v-html="mapResource.labelPaths"/>
        </defs>
        <g id="states" fill="#3e3e4b" opacity="1" stroke="#3a3a3a" stroke-width="0" font-family="Almendra SC" data-font="Almendra+SC" font-size="22" data-size="22" style="line-height:27px;text-anchor:middle;text-shadow:rgb(255, 255, 255) 0px 0px 4px;">
        <g v-for="(territory, index) in mapResource.territories" :key="index"
            font-family="Almendra SC"
            v-html="renderTerritory(territory, mapResource.names[index], mapResource.labels[index], index)"
            @click.stop="territoryClicked(index)" :class="[{clicked: selected===index,
            lastClicked: lastSelected===index}, currentAction]">
        </g>
        </g>
      </svg>
    </v-flex>
    <v-flex xs4 d-flex>
      <v-layout row fill-height>
<!--        <v-flex hidden-xs-only mdr pa-1>-->
<!--          <v-card height="100%">-->
<!--            <v-card-title>-->
<!--              <div class="headline">Map</div>-->
<!--            </v-card-title>-->
<!--          </v-card>-->
<!--        </v-flex>-->
        <v-flex sm12 md8 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">{{gamePhase}}</div>
            </v-card-title>
            <v-card-text>
              <v-list>
                <v-list-tile>Armies: {{armies}}</v-list-tile>
                <v-list-tile>
                  Action:
                  <v-layout row wrap>
                    <v-flex xs3 v-for="(key, action) in gameActions" :key="key">
                      <v-btn flat small :color="action === currentAction ? 'success' : 'normal'" @click.native="currentAction = action">
                        {{getActionName(action)}} ({{getActionShortcut(action)}})
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </v-list-tile>
              </v-list>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex md4 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">Armies for Attacking</div>
            </v-card-title>
            <v-card-text>
              <v-radio-group v-model="attackGroup">
                <v-radio
                  v-for="n in 3"
                  :key="n"
                  :label="`${n} Dice`"
                  :value="n"
                ></v-radio>
              </v-radio-group>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex md4 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">Armies for Defending</div>
            </v-card-title>
            <v-card-text>
              <v-radio-group v-model="defendGroup">
                <v-radio
                  v-for="n in 3"
                  :key="n"
                  :label="`${n} Dice`"
                  :value="n"
                ></v-radio>
              </v-radio-group>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex md4 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">Players</div>
            </v-card-title>
            <v-card-text>
              <v-list-tile
                v-for="[index, player] in players.entries()"
                :key="player.publicToken"
                :color="playerColors[player.publicToken]"
              >
                <v-list-tile-content>
                  <v-list-tile-title v-text="player.name"></v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-flex>
    <div id="app">
      <!--<button-->
        <!--type="button"-->
        <!--class="btn"-->
        <!--@click="showModal"-->
      <!--&gt;-->
        <!--Open Modal!-->
      <!--</button>-->
      <modal
        v-show="isModalVisible"
        @close="closeModalandUpdate"
        @fortify="getFortificationInfo($event)"
      />
    </div>

    <v-dialog v-model="gameEndDialog" persistent max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Game Over</span>
        </v-card-title>
        <v-card-text>
           <p class="text-md-center"> Winner: {{winnerName}} </p>
        </v-card-text>
        <v-flex shrink>
          <v-btn @click="newGame">New Game</v-btn>
        </v-flex>
      </v-card>
    </v-dialog>

  </v-layout>
</template>

<script>
import {mapGetters} from 'vuex'
import {PlaceArmy} from '@/models/packets'
import {gameActions, placeArmy, moveArmy, attack} from '@/models/game'
import {types} from '@/vuex/modules'
import modal from '@/views/modal.vue'
import { PlayAgain } from '../models/packets'

export default {
  name: 'Game',
  beforeMount () {
    if (this.$store.state.game.game.players.length === 0) {
      this.$router.replace({name: 'home'})
    }
  },
  components: {
    modal
  },
  data () {
    return {
      currentAction: gameActions.PLACE_ARMY,
      gameActions: gameActions,
      lastSelected: -1,
      selected: -1,
      myTurn: true,
      attackGroup: 1,
      defendGroup: 1,
      colors: ['red', 'blue', 'black', 'green', 'orange', 'violet'],
      isModalVisible: false,
      fortificationArmy: 0,
      dontReset: false,
      gameEndDialog: false
    }
  },
  computed: {
    ...mapGetters(['mapResource', 'getTurn', 'gamePublicToken', 'gamePhase', 'players', 'armies', 'gameToken', 'gameRoomRoomId']),
    playerColors () {
      var colorMap = {}
      var players = this.$store.state.game.players
      for (var i = 0; i < players.length; i++) {
        colorMap[players[i].publicToken] = this.colors[i]
      }
      return colorMap
    },
    winnerName () {
      var winnerToken = this.$store.state.game.winner
      return this.$store.state.game.players.find(p => p.publicToken === winnerToken) ? this.$store.state.game.players.find(p => p.publicToken === winnerToken).name : ''
    }
  },
  methods: {
    territoryClicked (id) {
      this.selected = id
      var owner = this.selected === -1 ? -1 : this.$store.state.game.game.territories[id].ownerToken
      if (this.gamePhase === 'Setup' && this.currentAction === gameActions.PLACE_ARMY) {
        if (this.selected !== -1) {
          if (this.getTurn === this.gamePublicToken) {
            if (owner === this.gamePublicToken || owner === '') {
              this.$socket.sendObj(new PlaceArmy(this.$store.state.game.token, this.$store.state.game.joinedRoom.roomId, id))
            } else {
              this.$toastr('warning', 'Cannot place army', 'This is not your territory')
            }
          } else {
            this.$toastr('warning', 'Cannot place army', 'This is not your turn')
          }
        }
      } else if (this.gamePhase === 'Realtime') {
        switch (this.currentAction) {
          case gameActions.PLACE_ARMY:
            if (this.selected !== -1) {
              if (owner === this.gamePublicToken || owner === '') {
                placeArmy(this.selected)
              } else {
                this.$toastr('warning', 'Cannot place army', 'This is not your territory')
              }
              this.selected = -1
            }
            break
          case gameActions.MOVE_ARMY:
            if (this.lastSelected !== -1 && this.selected !== -1) {
              if
              (this.$store.state.game.game.territories[this.lastSelected].armies > 1) {
                if (this.gamePublicToken ===
                  this.$store.state.game.game.territories[this.lastSelected].ownerToken) {
                  if (this.gamePublicToken === this.$store.state.game.game.territories[this.selected].ownerToken) {
                    if (this.$store.state.game.game.territories[this.lastSelected].neighbours.includes(this.selected)) {
                      this.dontReset = true
                      this.isModalVisible = true
                    } else {
                      this.$toastr('info', 'Please select an adjacent territory')
                    }
                  } else {
                    this.$toastr('info', 'Please select your own territory')
                  }
                } else {
                  this.$toastr('info', 'Please select your own territory')
                }
              } else {
                this.$toastr('info', 'Can\'t move army from a territory with 1 army only')
              }
            }
            break
          case gameActions.ATTACK:
            if (this.selected !== -1) {
              if (owner === this.gamePublicToken || owner === '' || this.lastSelected !== -1) {
                // Check valid game state
                if (this.lastSelected !== -1 && this.selected !== -1) {
                  // Selected 2 blocks
                  if (owner !== this.$store.state.game.game.territories[this.lastSelected].ownerToken) {
                    // Check if the attack origin is owned by current player.
                    if (this.$store.state.game.game.territories[this.lastSelected].armies > 1) {
                      // Check if origin has enough army (more than 1) to attack
                      let attackingArmyCount = this.$store.state.game.game.territories[this.lastSelected].armies
                      if (this.$store.state.game.game.territories[this.lastSelected].neighbours.includes(this.selected)) {
                        // Check Neighbor
                        if (attackingArmyCount <= 3) {
                          if (this.attackGroup <= attackingArmyCount - 1) {
                            this.$toastr('success', 'Attacking!')
                            attack(this.lastSelected, this.selected, this.attackGroup)
                          } else {
                            this.$toastr('warning', 'Reduce number of dices')
                          }
                        } else {
                          this.$toastr('success', 'Attacking!')
                          attack(this.lastSelected, this.selected, this.attackGroup)
                        }
                      } else {
                        this.$toastr('warning', 'Cannot attack', 'Not an adjacent territory')
                      }
                    } else {
                      this.$toastr('warning', 'Cannot attack', 'Not enough armies')
                    }
                  } else {
                    this.$toastr('warning', 'Cannot attack', 'Cannot attack own territory')
                  }
                  this.selected = -1
                } else if (this.lastSelected === -1) {
                  this.$toastr('info', 'Select territory to attack', 'Select adjacent territory to attack')
                }
              } else {
                this.$toastr('warning', 'Cannot place army', 'This is not your territory')
                this.selected = -1
              }
            }
            break
        }
        if (!this.dontReset) {
          this.lastSelected = this.selected
        }
      }
    },
    renderTerritory (territory, name, label, index) {
      var owner = this.$store.state.game.game.territories[index].ownerToken
      let htmlObject = document.createElement('div')
      htmlObject.innerHTML = label
      htmlObject.getElementsByTagName('textPath')['0'].innerHTML = `${name} - ${this.$store.state.game.game.territories[index].armies}`
      if (owner === '') {
        htmlObject.getElementsByTagName('textPath')['0'].setAttribute('style', 'fill:#d4aa00;stroke-width:0.25;stroke:black')
      } else {
        htmlObject.getElementsByTagName('textPath')['0'].setAttribute('style', 'fill:' + this.playerColors[owner] + ';stroke-width:0.25;stroke:black')
      }
      return territory + htmlObject.firstChild.outerHTML
    },
    newGame () {
      this.$socket.sendObj(new PlayAgain(this.gameToken, this.gameRoomRoomId))
      this.$router.replace({name: 'Lobby'})
      this.$router.go()
    },
    getActionName (action) {
      switch (action) {
        case gameActions.PLACE_ARMY:
          return 'Place'
        case gameActions.MOVE_ARMY:
          return 'Move'
        case gameActions.ATTACK:
          return 'Attack'
      }
    },
    getActionShortcut (action) {
      switch (action) {
        case gameActions.PLACE_ARMY:
          return 'Q'
        case gameActions.MOVE_ARMY:
          return 'W'
        case gameActions.ATTACK:
          return 'E'
      }
    },
    showModal () {
      this.isModalVisible = true
    },
    closeModalandUpdate () {
      this.isModalVisible = false
      this.selected = -1
      this.lastSelected = this.selected
      this.dontReset = false
    },
    closeModal () {
      this.isModalVisible = false
    },
    getFortificationInfo (inputArmy) {
      this.fortificationArmy = inputArmy
      if (!isNaN(inputArmy)) {
        if (inputArmy > 0) {
          if (inputArmy <
            this.$store.state.game.game.territories[this.lastSelected].armies) {
            this.fortify()
          } else {
            this.$toastr('info',
              'Armies for fortification needs to be less than armies at current territory')
            this.closeModal()
            this.showModal()
          }
        } else {
          this.$toastr('info', 'Armies for fortification needs to be greater than 0')
          this.closeModal()
          this.showModal()
        }
      } else {
        this.$toastr('info', 'Armies for fortification needs to be a number')
        this.closeModal()
        this.showModal()
      }
      // this.$toastr('info', 'Armies moved: ', this.fortificationArmy)
    },
    fortify () {
      moveArmy(this.lastSelected, this.selected, this.fortificationArmy)
      this.closeModalandUpdate()
    }
  },
  mounted () {
    this.$store.watch(
      (state) => state.game.game.phase,
      () => {
        if (this.gamePhase === 'Realtime') {
          this.$toastr('info', 'Realtime mode has started!')
        }
      }
    )

    this.$store.subscribe((mutation, state) => {
      if (mutation.type === types.NOTIFY_GAME_END) {
        console.log('Game ended')
        this.gameEndDialog = true
      }
    })

    window.addEventListener('keydown', e => {
      switch (e.code) {
        case 'KeyQ':
          console.log('Place Army')
          this.currentAction = gameActions.PLACE_ARMY
          break
        case 'KeyW':
          console.log('Move army')
          this.currentAction = gameActions.MOVE_ARMY
          break
        case 'KeyE':
          console.log('Attack')
          this.currentAction = gameActions.ATTACK
          break
      }
    })
  }
}
</script>

<style scoped>
  svg {
    overflow: hidden;
  }

  .PLACE_ARMY {
    stroke: white;
    stroke-width: 0;
  }

  .MOVE_ARMY {
    stroke: #FFFF8D;
    stroke-width: 0;
  }

  .ATTACK {
    stroke: #FF8A80;
    stroke-width: 0;
  }

  svg > g:hover {
    stroke-width: 0.5;
  }

  .clicked, .clicked:hover {
    stroke-width: 1.5;
  }
</style>
